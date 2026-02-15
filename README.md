# Chronolog

Spring Boot 初学者向けの勤怠管理アプリケーションです。
MVC パターン、依存性注入（DI）、Bean 管理など Spring Boot の基本概念を実践的に学ぶことを目的としています。

## 技術スタック

| カテゴリ | 技術 |
|---------|------|
| フレームワーク | Spring Boot 3.x |
| ビルドツール | Maven |
| 言語 | Java 17+ |
| データベース | H2 Database（ファイルベース） |
| ORM | Spring Data JPA |
| テンプレートエンジン | Thymeleaf |
| セキュリティ | Spring Security 6.x |
| テスト | JUnit 5 / Mockito / AssertJ / jqwik（PBT） |

## 主な機能

- **ユーザー認証** - ユーザー登録・ログイン・ログアウト（Spring Security / BCrypt）
- **出勤記録** - ボタンクリックで現在時刻を出勤時刻として記録
- **退勤記録** - ボタンクリックで現在時刻を退勤時刻として記録
- **勤怠履歴** - 自分の勤怠レコードを日付降順で一覧表示
- **勤務時間計算** - 出勤・退勤時刻から勤務時間を自動計算（X時間Y分形式）
- **バリデーション** - 重複出勤/退勤の防止、出勤記録なしでの退勤拒否

## アーキテクチャ

3 層アーキテクチャ（MVC）を採用し、各レイヤーはインターフェースを介してコンストラクタインジェクションで接続されます。

```mermaid
graph TD
    A[プレゼンテーション層<br/>AttendanceController / AuthController<br/>Thymeleaf テンプレート] --> B[ビジネスロジック層<br/>AttendanceService<br/>CustomUserDetailsService]
    B --> C[データアクセス層<br/>AttendanceRepository / UserRepository<br/>Spring Data JPA]
    C --> D[(H2 Database)]
    E[セキュリティ層<br/>Spring Security<br/>SecurityConfig] --> A
```

## データモデル

```mermaid
erDiagram
    User {
        Long id PK
        String username "UNIQUE"
        String password "BCrypt暗号化"
        String employeeId
        String role "ROLE_USER / ROLE_ADMIN"
        boolean enabled
    }

    AttendanceRecord {
        Long id PK
        String employeeId
        LocalDate workDate
        LocalDateTime clockInTime
        LocalDateTime clockOutTime "NULL許容"
    }

    User ||--o{ AttendanceRecord : "employeeId"
```

## 主要フロー

### 出勤・退勤フロー

```mermaid
sequenceDiagram
    participant U as ユーザー
    participant C as Controller
    participant S as Service
    participant R as Repository

    U->>C: 出勤ボタンをクリック
    C->>S: clockIn(employeeId)
    S->>R: 当日の重複チェック
    alt 重複なし
        S->>R: 新規レコード保存
        S-->>C: 成功
        C-->>U: 成功メッセージ表示
    else 既に出勤済み
        S-->>C: DuplicateClockInException
        C-->>U: エラーメッセージ表示
    end

    U->>C: 退勤ボタンをクリック
    C->>S: clockOut(employeeId)
    S->>R: 当日の出勤記録を検索
    alt 出勤記録あり・未退勤
        S->>R: 退勤時刻を更新
        S-->>C: 成功
        C-->>U: 成功メッセージ表示
    else 出勤記録なし
        S-->>C: NoClockInRecordException
        C-->>U: エラーメッセージ表示
    end
```

## プロジェクト構成

```
src/
├── main/
│   ├── java/com/example/attendance/
│   │   ├── controller/    # AttendanceController, AuthController
│   │   ├── service/       # AttendanceService, CustomUserDetailsService
│   │   ├── repository/    # AttendanceRepository, UserRepository
│   │   ├── entity/        # AttendanceRecord, User
│   │   ├── config/        # SecurityConfig
│   │   └── exception/     # カスタム例外クラス
│   └── resources/
│       ├── application.properties
│       └── templates/     # login, register, home, history
└── test/
    └── java/com/example/attendance/
        ├── service/       # ユニットテスト / プロパティテスト
        ├── controller/    # コントローラテスト
        ├── security/      # セキュリティテスト
        └── integration/   # 統合テスト
```

## セットアップ

```bash
# ビルド
./mvnw clean package

# 起動
./mvnw spring-boot:run
```

起動後、http://localhost:8080 にアクセスしてください。

## 学習ポイント

| 概念 | 学べること |
|------|-----------|
| MVC アーキテクチャ | Controller / Service / Repository の責務分離 |
| 依存性注入（DI） | コンストラクタインジェクションによる疎結合 |
| Spring Data JPA | リポジトリパターンとクエリメソッド |
| Thymeleaf | サーバーサイドテンプレートエンジン |
| Spring Security | 認証・認可、フォームログイン、セッション管理 |
| パスワード暗号化 | BCryptPasswordEncoder の使用 |
| エラーハンドリング | カスタム例外とユーザーフレンドリーなメッセージ |
| テスト | BDD（Given-When-Then）とプロパティベーステスト |
