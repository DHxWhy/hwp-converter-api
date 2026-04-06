# HWP Converter API

한글(HWP) 파일을 텍스트로 변환하는 경량 API 서버입니다.

[NomaDamas/hwp-converter-api](https://github.com/NomaDamas/hwp-converter-api)를 기반으로 개선했습니다.

## 개선 사항

| 항목 | 원본 | 이 포크 |
|------|------|---------|
| hwplib 버전 | 1.0.1 (2023) | **1.1.10** (2025) |
| 에러 핸들링 | 없음 (서버 크래시) | **try-catch + 폴백** |
| Docker 빌드 | 미리 빌드된 JAR | **멀티스테이지 빌드** |
| 헬스체크 | 없음 | **GET /health** |

### hwplib 1.1.10 업그레이드

- `NullPointerException` (테이블 내 특수 컨트롤) 수정
- `This is not paragraph` 에러 수정
- 다양한 HWP 파일 변형 호환성 향상

### 에러 핸들링

테이블 파싱 실패 시 본문만 추출하는 폴백 처리:
```
1차: 전체 파싱 (테이블 포함)
  ↓ 실패 시
2차: 본문만 파싱 (테이블 제외)
  ↓ 실패 시
400 응답 (서버 크래시 없음)
```

## API

### POST /upload

HWP 파일을 텍스트로 변환합니다.

```bash
# 테이블 포함
curl -X POST -F 'file=@document.hwp' http://localhost:7000/upload?option=all

# 본문만
curl -X POST -F 'file=@document.hwp' http://localhost:7000/upload?option=main-only
```

### GET /health

헬스체크 엔드포인트.

## 배포

### Docker

```bash
docker build -t hwp-converter-api .
docker run -p 7000:7000 hwp-converter-api
```

### Render / Railway

Git 레포 연결 후 자동 빌드. Dockerfile 기반.

## 라이선스

Apache-2.0 (원본 라이선스 유지)

## 원본

- [NomaDamas/hwp-converter-api](https://github.com/NomaDamas/hwp-converter-api)
- [hwplib](https://github.com/neolord0/hwplib) by neolord0
