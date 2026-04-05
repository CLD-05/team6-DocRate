FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY import_csv.py .
COPY departments.csv .
COPY 건강_병원.csv .

CMD ["python", "import_csv.py"]
