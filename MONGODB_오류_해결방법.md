# ⚠️ MongoDB 인증 오류 해결 방법

## 🔴 현재 오류
```
SCRAM failure: bad auth : authentication failed
```

**원인**: Vercel MongoDB 연결 정보가 유효하지 않거나 만료되었습니다.

---

## ✅ 해결 방법

### 1단계: Vercel Dashboard에서 새 연결 문자열 받기

1. **Vercel Dashboard 접속**: https://vercel.com/dashboard
2. **프로젝트 선택**: `aw-portfolio` 클릭
3. **Storage 탭** 클릭
4. **MongoDB 항목** 클릭
5. **새로운 Connection String 복사**

#### 📍 중요: 연결 문자열 형식 확인
올바른 형식:
```
mongodb+srv://[USERNAME]:[PASSWORD]@[CLUSTER].mongodb.net/[DATABASE_NAME]?retryWrites=true&w=majority
```

예시:
```
mongodb+srv://admin:Abc123xyz@cluster0.abc123.mongodb.net/portfolioDB?retryWrites=true&w=majority
```

⚠️ **필수**: 연결 문자열에 **데이터베이스 이름**이 반드시 포함되어야 합니다!

---

### 2단계: .env 파일 업데이트

프로젝트 루트의 `.env` 파일을 열고 `DATABASE_URL`을 새로 복사한 연결 문자열로 교체:

```env
DATABASE_URL="여기에_새로_복사한_연결_문자열_붙여넣기"
JWT_SECRET="F8cjHwRaWUe1ffMk9ccaps1hwQ4rakuQARbA6lH1Mws="
NEXTAUTH_SECRET="BKKUatsUYpdqMOSZPy1aGaVL6IpCGayA82UVglOlupk="
NEXTAUTH_URL="http://localhost:3000"
NODE_ENV="development"
```

---

### 3단계: 개발 서버 재시작

```bash
# 현재 서버 중지 (Ctrl+C)
# 그 다음 아래 명령어 실행:

npm run dev
```

---

## 🆘 대체 방법: 직접 MongoDB Atlas 설정

Vercel Storage가 계속 문제가 있다면 MongoDB Atlas를 직접 사용:

### A. MongoDB Atlas 계정 생성 (무료)
1. https://www.mongodb.com/cloud/atlas/register 접속
2. 무료 계정 생성
3. 무료 클러스터 생성 (M0 Free Tier)

### B. 데이터베이스 사용자 생성
1. **Database Access** 메뉴
2. **Add New Database User** 클릭
3. **Username**: `admin` (원하는 이름)
4. **Password**: 강력한 비밀번호 생성 (**특수문자 주의!**)
5. **Built-in Role**: Read and Write to any database
6. **Add User** 클릭

### C. IP 화이트리스트 설정
1. **Network Access** 메뉴
2. **Add IP Address** 클릭
3. **Allow Access from Anywhere** 선택 (0.0.0.0/0)
4. **Confirm** 클릭

### D. 연결 문자열 가져오기
1. **Database** 메뉴로 돌아가기
2. **Connect** 버튼 클릭
3. **Connect your application** 선택
4. **Connection string** 복사
5. `<password>`를 실제 비밀번호로 교체
6. `myFirstDatabase`를 원하는 데이터베이스 이름으로 변경 (예: `portfolioDB`)

**예시**:
```
mongodb+srv://admin:MyPassword123@cluster0.abc123.mongodb.net/portfolioDB?retryWrites=true&w=majority
```

⚠️ **비밀번호에 특수문자가 있으면 URL 인코딩 필요!**
- `@` → `%40`
- `#` → `%23`
- `/` → `%2F`
- `?` → `%3F`

---

## 📝 체크리스트

- [ ] Vercel Dashboard에서 새 연결 문자열 복사 (또는 MongoDB Atlas 직접 설정)
- [ ] 연결 문자열에 데이터베이스 이름 포함 확인
- [ ] `.env` 파일의 `DATABASE_URL` 업데이트
- [ ] 파일 저장 (Ctrl+S)
- [ ] 개발 서버 재시작 (`npm run dev`)
- [ ] 브라우저에서 http://localhost:3001 테스트

---

## ✅ 성공 확인 방법

터미널에 이런 오류가 **없으면** 성공:
```
❌ SCRAM failure: bad auth : authentication failed
```

성공하면 페이지가 정상적으로 로드되고 "제출자 정보 입력" 폼이 작동합니다!

---

## 💡 추가 도움

문제가 계속되면:
1. `.env` 파일의 연결 문자열 형식 다시 확인
2. MongoDB Atlas IP 화이트리스트 확인 (0.0.0.0/0 허용)
3. 비밀번호에 특수문자가 있으면 URL 인코딩
4. Vercel Storage 대신 MongoDB Atlas 직접 사용 고려

