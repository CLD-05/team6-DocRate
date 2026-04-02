import os
import pandas as pd
import pymysql
import re

# docker 서팅
# DB_HOST = os.getenv("DB_HOST")
# DB_PORT = int(os.getenv("DB_PORT"))
# DB_NAME = os.getenv("DB_NAME")
# DB_USER = os.getenv("DB_USER")
# DB_PASSWORD = os.getenv("DB_PASSWORD")
#
# CSV_PATH = os.getenv("CSV_PATH")

# 로컬 세팅
DB_HOST = "localhost"
DB_PORT = 3306
DB_NAME = "app"
DB_USER = "root"
DB_PASSWORD = "CLSRn090!"

CSV_PATH_USERS = os.getenv("CSV_PATH", "./users_200.csv")
CSV_PATH_HOSPITALS = os.getenv("CSV_PATH", "./건강_병원.csv")
CSV_PATH_DOCTORS = os.getenv("CSV_PATH", "./doctors_200.csv")
CSV_PATH_REVIEWS = os.getenv("CSV_PATH", "./reviews_200.csv")


def normalize_text(x):
    if pd.isna(x):
        return None

    s = str(x).strip()

    s = s.replace("\r", " ").replace("\n", " ").replace("\t", " ")

    s = re.sub(r"\s+", " ", s)

    s = s.strip()

    return s if s else None

def preprocess(csv_path):
    df = pd.read_csv(csv_path, encoding="cp949", encoding_errors="ignore")

    # 컬럼명 변경
    df = df.rename(columns={
        "사업장명": "name",
        "도로명주소": "road_address",
        "지번주소": "lot_address",
        "전화번호": "phone",
        "의료기관종별명": "medical_type",
        "업태구분명": "business_type",
        "상세영업상태명": "status_raw"
    })

    # 문자열 공백 제거
    for col in df.columns:
        if df[col].dtype == "object":
            df[col] = df[col].astype(str).str.strip()

    # 빈 문자열/가짜 null 처리
    df = df.replace({
        "": pd.NA,
        "nan": pd.NA,
        "NaN": pd.NA,
        "None": pd.NA
    })

    # 주소 선택: 도로명주소 우선, 없으면 지번주소
    df["address"] = df["road_address"].fillna(df["lot_address"])

    # name/address 정규화
    df["name"] = df["name"].apply(normalize_text)
    df["address"] = df["address"].apply(normalize_text)

    # 병원종별 선택
    df["category"] = df["medical_type"].fillna(df["business_type"])
    df["category"] = df["category"].fillna("기타")
    df["category"] = df["category"].astype(str).str.strip()

    df.loc[df["category"] == "", "category"] = "기타"
    df.loc[df["category"].isin(["nan", "None"]), "category"] = "기타"

    # 전화번호 정리
    def clean_phone(x):
        if pd.isna(x):
            return None
        s = str(x).strip()
        if s.endswith(".0"):
            s = s[:-2]

        digits = "".join(ch for ch in s if ch.isdigit())

        if not digits:
            return None

        # 서울 번호
        if digits.startswith("2"):
            if len(digits) == 8:
                return f"02-{digits[2:5]}-{digits[5:]}"
            if len(digits) == 9:
                return f"02-{digits[2:6]}-{digits[6:]}"
            return digits

        # 일반 번호
        if len(digits) == 8:
            return f"{digits[:4]}-{digits[4:]}"
        if len(digits) == 9:
            return f"0{digits[:2]}-{digits[3:5]}-{digits[5:]}"
        if len(digits) == 10:
            return f"{digits[:3]}-{digits[3:6]}-{digits[6:]}"
        return digits

    df["phone"] = df["phone"].apply(clean_phone)

    # 상태값 정규화
    def map_status(x):
        if pd.isna(x):
            return "ACTIVE"

        s = str(x).strip()

        inactive_keywords = [
            "폐업", "취소", "말소", "만료",
            "정지", "중지", "휴업",
            "삭제", "전출", "제외"
        ]

        if any(keyword in s for keyword in inactive_keywords):
            return "INACTIVE"

        return "ACTIVE"

    df["status"] = df["status_raw"].apply(map_status)

    # 필요한 컬럼만 선택
    df = df[["name", "address", "phone", "category", "status"]]

    # 필수값 없는 행 제거
    df = df.dropna(subset=["name", "address"])

    # 길이 제한
    df["name"] = df["name"].astype(str).str.slice(0, 100)
    df["address"] = df["address"].astype(str).str.slice(0, 255)
    df["phone"] = df["phone"].astype("string").str.slice(0, 20)
    df["category"] = df["category"].astype("string").str.slice(0, 50)
    df["status"] = df["status"].astype(str).str.slice(0, 20)

    # 중복 제거
    df = df.drop_duplicates(subset=["name", "address"], keep="last")

    return df

def reset_database():
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT,
        charset="utf8mb4",
        autocommit=False
    )

    cursor = conn.cursor()

    try:
        print("DB 초기화 시작...")

        # FK OFF
        cursor.execute("SET FOREIGN_KEY_CHECKS = 0")

        # TRUNCATE (빠르고 AUTO_INCREMENT 초기화됨)
        cursor.execute("TRUNCATE TABLE reviews")
        cursor.execute("TRUNCATE TABLE doctor_requests")
        cursor.execute("TRUNCATE TABLE doctors")
        cursor.execute("TRUNCATE TABLE hospital_requests")
        cursor.execute("TRUNCATE TABLE departments")
        cursor.execute("TRUNCATE TABLE hospitals")
        cursor.execute("TRUNCATE TABLE users")

        # FK ON
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1")

        conn.commit()
        print("DB 초기화 완료")

    except Exception as e:
        conn.rollback()
        print("DB 초기화 실패:", e)
        raise

    finally:
        cursor.close()
        conn.close()

def load_departments_to_db():
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT,
        charset="utf8mb4",
        autocommit=False
    )

    cursor = conn.cursor()

    # 기본 진료과 (필요하면 추가 가능)
    departments = [
        (1, "정형외과"),
        (2, "내과"),
        (3, "피부과"),
        (4, "소아과"),
        (5, "신경과")
    ]

    sql = """
    INSERT INTO departments (id, name)
    VALUES (%s, %s)
    ON DUPLICATE KEY UPDATE
    name = VALUES(name)
    """

    try:
        cursor.executemany(sql, departments)
        conn.commit()
        print(f"departments 적재 완료: {len(departments)}건")
    except Exception as e:
        conn.rollback()
        print("departments 적재 실패:", e)
        raise
    finally:
        cursor.close()
        conn.close()


def load_to_db_dml(df):
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT,
        charset="utf8mb4",
        autocommit=False
    )

    cursor = conn.cursor()
    cursor.execute("SET NAMES utf8mb4")

    sql = """
    INSERT INTO hospitals (name, address, phone, category, status)
    VALUES (%s, %s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE
    phone = VALUES(phone),
    category = VALUES(category),
    status = VALUES(status)
    """

    data = [
        (
            row["name"],
            row["address"],
            None if pd.isna(row["phone"]) else row["phone"],
            None if pd.isna(row["category"]) else row["category"],
            row["status"]
        )
        for _, row in df.iterrows()
    ]

    try:
        cursor.executemany(sql, data)
        conn.commit()
        print(f"적재 완료: {len(data)}건")
    except Exception as e:
        conn.rollback()
        print("DB 적재 실패:", e)
        raise
    finally:
        cursor.close()
        conn.close()

def load_users_to_db(df):
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT,
        charset="utf8mb4",
        autocommit=False
    )

    cursor = conn.cursor()

    sql = """
    INSERT INTO users (email, password, nickname, role)
    VALUES (%s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname),
    role = VALUES(role)
    """

    data = [
        (
            row["email"],
            row["password"],
            row["nickname"],
            row["role"]
        )
        for _, row in df.iterrows()
    ]

    try:
        cursor.executemany(sql, data)
        conn.commit()
        print(f"users 적재 완료: {len(data)}건")
    except Exception as e:
        conn.rollback()
        print("users 적재 실패:", e)
        raise
    finally:
        cursor.close()
        conn.close()

def load_doctors_to_db(df):
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT,
        charset="utf8mb4",
        autocommit=False
    )

    cursor = conn.cursor()

    sql = """
    INSERT INTO doctors (hospital_id, department_id, name, intro, status)
    VALUES (%s, %s, %s, %s, %s)
    """

    data = [
        (
            int(row["hospital_id"]),
            int(row["department_id"]),
            row["name"],
            row["intro"],
            row["status"]
        )
        for _, row in df.iterrows()
    ]

    try:
        cursor.executemany(sql, data)
        conn.commit()
        print(f"doctors 적재 완료: {len(data)}건")
    except Exception as e:
        conn.rollback()
        print("doctors 적재 실패:", e)
        raise
    finally:
        cursor.close()
        conn.close()

def load_reviews_to_db(df):
    conn = pymysql.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        port=DB_PORT,
        charset="utf8mb4",
        autocommit=False
    )

    cursor = conn.cursor()

    sql = """
    INSERT INTO reviews (
        doctor_id, user_id, rating,
        bedside_manner, explanation, wait_time,
        revisit_intention, content
    )
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    """

    data = [
        (
            int(row["doctor_id"]),
            int(row["user_id"]),
            int(row["rating"]),
            int(row["bedside_manner"]),
            int(row["explanation"]),
            int(row["wait_time"]),
            1 if str(row["revisit_intention"]).lower() == "true" else 0,
            row["content"]
        )
        for _, row in df.iterrows()
    ]

    try:
        cursor.executemany(sql, data)
        conn.commit()
        print(f"reviews 적재 완료: {len(data)}건")
    except Exception as e:
        conn.rollback()
        print("reviews 적재 실패:", e)
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    reset_database()
    load_departments_to_db()
    df = preprocess(CSV_PATH_HOSPITALS)

    print("전처리 결과 샘플:")
    print(df.head())
    print(f"\n전처리 후 데이터 개수: {len(df)}")

    load_to_db_dml(df)
    df_users = pd.read_csv(CSV_PATH_USERS, encoding="utf-8")
    load_users_to_db(df_users)
    df_doctors = pd.read_csv(CSV_PATH_DOCTORS, encoding="utf-8")
    load_doctors_to_db(df_doctors)
    df_reviews = pd.read_csv(CSV_PATH_REVIEWS, encoding="utf-8")
    load_reviews_to_db(df_reviews)
