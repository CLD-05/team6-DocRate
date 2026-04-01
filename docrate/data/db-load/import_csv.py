import os
import pandas as pd
import pymysql
import re

# DB_HOST = os.getenv("DB_HOST")
# DB_PORT = int(os.getenv("DB_PORT"))
# DB_NAME = os.getenv("DB_NAME")
# DB_USER = os.getenv("DB_USER")
# DB_PASSWORD = os.getenv("DB_PASSWORD")
#
# CSV_PATH = os.getenv("CSV_PATH")

DB_HOST = "localhost"
DB_PORT = 3306
DB_NAME = "app"
DB_USER = "root"
DB_PASSWORD = "Dudgus369##*"

CSV_PATH = os.getenv("CSV_PATH", "./건강_병원.csv")

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
        if "폐업" in s or "취소" in s or "말소" in s:
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


if __name__ == "__main__":
    df = preprocess(CSV_PATH)

    print("전처리 결과 샘플:")
    print(df.head())
    print(f"\n전처리 후 데이터 개수: {len(df)}")

    load_to_db_dml(df)
