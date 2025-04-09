FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y bash

COPY requirements.txt .
COPY get_cultural_events.py .
RUN pip install -r requirements.txt

COPY . .

CMD ["tail", "-f", "/dev/null"]
