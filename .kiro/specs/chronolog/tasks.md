# 実装計画: 勤怠管理システム

## 概要

本実装計画は、Spring Boot初学者向けの勤怠管理アプリケーションを段階的に構築するためのタスクリストです。各タスクは、MVCアーキテクチャ、依存性注入、Spring Security、プロパティベーステストなどの概念を実践的に学べるように設計されています。

## タスク

- [x] 1. プロジェクトのセットアップと基本構造の作成
  - Spring Initializrを使用してプロジェクトを生成（Spring Boot 3.x、Java 17、Maven）
  - 必要な依存関係を追加（Web、JPA、H2、Thymeleaf、Security、jqwik）
  - パッケージ構造を作成（controller、service、repository、entity、config、exception）
  - application.propertiesにH2データベースとThymeleafの設定を追加
  - _要件: 6.1, 7.1, 7.2, 7.3_

- [x] 2. エンティティクラスの実装
  - [x] 2.1 AttendanceRecordエンティティを作成
    - @Entity、@Table、@Id、@GeneratedValue、@Columnアノテーションを使用
    - id、employeeId、workDate、clockInTime、clockOutTimeフィールドを定義
    - コンストラクタ、ゲッター、セッターを実装
    - _要件: 1.2, 2.2_

  - [x] 2.2 Userエンティティを作成
    - @Entity、@Table、@Id、@GeneratedValue、@Columnアノテーションを使用
    - id、username、password、employeeId、role、enabledフィールドを定義
    - usernameにUNIQUE制約を設定
    - コンストラクタ、ゲッター、セッターを実装
    - _要件: 12.1, 13.2_

- [x] 3. リポジトリインターフェースの実装
  - [x] 3.1 AttendanceRepositoryインターフェースを作成
    - JpaRepositoryを継承
    - findByEmployeeIdOrderByWorkDateDescメソッドを定義
    - findByEmployeeIdAndWorkDateメソッドを定義
    - existsByEmployeeIdAndWorkDateメソッドを定義
    - _要件: 3.1, 5.1_

  - [x] 3.2 UserRepositoryインターフェースを作成
    - JpaRepositoryを継承
    - findByUsernameメソッドを定義
    - findByEmployeeIdメソッドを定義
    - existsByUsernameメソッドを定義
    - _要件: 11.2, 12.3_

- [ ] 4. カスタム例外クラスの実装
  - DuplicateClockInException、NoClockInRecordException、DuplicateClockOutException、InvalidInputExceptionを作成
  - 各例外はRuntimeExceptionを継承
  - コンストラクタでメッセージを受け取る
  - _要件: 8.1_

- [ ] 5. サービス層の実装
  - [ ] 5.1 AttendanceServiceインターフェースを作成
    - clockIn、clockOut、getAttendanceHistory、calculateWorkingHoursメソッドを定義
    - _要件: 1.1, 2.1, 3.1, 4.1_
  
  - [ ] 5.2 AttendanceServiceImplクラスを実装
    - @Serviceアノテーションを付与
    - AttendanceRepositoryをコンストラクタインジェクション
    - clockInメソッドを実装（重複チェック、新規レコード作成）
    - clockOutメソッドを実装（出勤記録チェック、退勤時刻更新）
    - getAttendanceHistoryメソッドを実装（日付降順で取得）
    - calculateWorkingHoursメソッドを実装（Duration計算）
    - 入力バリデーション（従業員IDのnullチェック）を実装
    - _要件: 1.1, 1.2, 1.4, 2.1, 2.2, 2.4, 2.5, 3.1, 4.1, 4.2, 9.1_
  
  - [ ]* 5.3 AttendanceServiceのプロパティテストを作成
    - **プロパティ1: 出勤記録の作成と完全性**
    - **検証: 要件 1.1, 1.2**
  
  - [ ]* 5.4 AttendanceServiceのプロパティテストを作成
    - **プロパティ2: 重複出勤の防止**
    - **検証: 要件 1.4**
  
  - [ ]* 5.5 AttendanceServiceのプロパティテストを作成
    - **プロパティ3: 退勤記録の更新**
    - **検証: 要件 2.1, 2.2**
  
  - [ ]* 5.6 AttendanceServiceのプロパティテストを作成
    - **プロパティ4: 出勤記録なしでの退勤拒否**
    - **検証: 要件 2.4**
  
  - [ ]* 5.7 AttendanceServiceのプロパティテストを作成
    - **プロパティ5: 重複退勤の防止**
    - **検証: 要件 2.5**
  
  - [ ]* 5.8 AttendanceServiceのプロパティテストを作成
    - **プロパティ6: 勤怠履歴の日付降順ソート**
    - **検証: 要件 3.1**
  
  - [ ]* 5.9 AttendanceServiceのプロパティテストを作成
    - **プロパティ8: 勤務時間の正確な計算**
    - **検証: 要件 4.1, 4.2**
  
  - [ ]* 5.10 AttendanceServiceのプロパティテストを作成
    - **プロパティ10: データ永続化のラウンドトリップ**
    - **検証: 要件 5.1**
  
  - [ ]* 5.11 AttendanceServiceのプロパティテストを作成
    - **プロパティ13: 従業員ID必須バリデーション**
    - **検証: 要件 9.1**
  
  - [ ]* 5.12 AttendanceServiceのユニットテストを作成
    - 退勤時刻がnullの場合の勤務時間計算テスト
    - 勤怠履歴が空の場合のテスト
    - エッジケースのテスト
    - _要件: 3.4, 4.4_

- [ ] 6. チェックポイント - サービス層のテストを実行
  - すべてのテストが成功することを確認
  - 質問があればユーザーに確認

- [ ] 7. Spring Securityの設定
  - [ ] 7.1 CustomUserDetailsServiceを実装
    - @Serviceアノテーションを付与
    - UserDetailsServiceインターフェースを実装
    - UserRepositoryをコンストラクタインジェクション
    - loadUserByUsernameメソッドを実装（ユーザー検索、UserDetails変換）
    - _要件: 11.2, 11.3_
  
  - [ ] 7.2 SecurityConfigクラスを作成
    - @Configuration、@EnableWebSecurityアノテーションを付与
    - SecurityFilterChainをBean登録
    - 認証不要パス（/login、/register、/css/**、/h2-console/**）を設定
    - 認証必須パス（その他すべて）を設定
    - フォームログイン設定（ログインページ、成功時リダイレクト先）
    - ログアウト設定
    - CSRF設定（H2コンソール除外）
    - PasswordEncoderをBean登録（BCryptPasswordEncoder）
    - _要件: 11.1, 11.4, 11.5, 13.1, 13.4, 14.1, 14.2_
  
  - [ ]* 7.3 SecurityConfigのプロパティテストを作成
    - **プロパティ14: パスワードの暗号化**
    - **検証: 要件 13.1, 13.2**
  
  - [ ]* 7.4 SecurityConfigのプロパティテストを作成
    - **プロパティ15: 認証されたユーザーのみアクセス可能**
    - **検証: 要件 11.4**

- [ ] 8. コントローラ層の実装
  - [ ] 8.1 AuthControllerを作成
    - @Controllerアノテーションを付与
    - UserRepository、PasswordEncoderをコンストラクタインジェクション
    - loginメソッドを実装（GET /login、ログインページ表示）
    - registerFormメソッドを実装（GET /register、登録ページ表示）
    - registerメソッドを実装（POST /register、ユーザー登録処理）
    - ユーザー名重複チェック、パスワード暗号化、デフォルトロール設定
    - _要件: 11.1, 12.1, 12.2, 12.3, 12.4, 12.5, 13.1_
  
  - [ ]* 8.2 AuthControllerのプロパティテストを作成
    - **プロパティ16: ユーザー名の一意性**
    - **検証: 要件 12.3**
  
  - [ ]* 8.3 AuthControllerのユニットテストを作成
    - ログインページ表示テスト
    - 登録ページ表示テスト
    - 必須項目未入力時のテスト
    - _要件: 11.1, 12.1, 12.4_
  
  - [ ] 8.4 AttendanceControllerを作成
    - @Controller、@RequestMapping("/attendance")アノテーションを付与
    - AttendanceService、UserRepositoryをコンストラクタインジェクション
    - homeメソッドを実装（GET /、Principalから従業員ID取得、ホームページ表示）
    - clockInメソッドを実装（POST /clock-in、出勤処理、成功/エラーメッセージ）
    - clockOutメソッドを実装（POST /clock-out、退勤処理、成功/エラーメッセージ）
    - historyメソッドを実装（GET /history、勤怠履歴取得、勤務時間計算、表示）
    - 例外ハンドリング（try-catch、RedirectAttributes）
    - _要件: 1.1, 1.3, 2.1, 2.3, 3.1, 3.2, 8.2, 8.4, 10.1, 10.2, 10.3, 15.1, 15.2, 15.3, 15.4_
  
  - [ ]* 8.5 AttendanceControllerのプロパティテストを作成
    - **プロパティ12: エラーメッセージの伝達**
    - **検証: 要件 8.4**
  
  - [ ]* 8.6 AttendanceControllerのプロパティテストを作成
    - **プロパティ17: ログインユーザーに基づく勤怠記録**
    - **検証: 要件 15.1, 15.2, 15.3**
  
  - [ ]* 8.7 AttendanceControllerのユニットテストを作成
    - ホームページ表示テスト
    - 出勤成功時のテスト
    - 退勤成功時のテスト
    - 勤怠履歴表示テスト
    - 例外発生時のエラーメッセージテスト
    - _要件: 1.3, 2.3, 8.2, 8.4_

- [ ] 9. チェックポイント - コントローラ層のテストを実行
  - すべてのテストが成功することを確認
  - 質問があればユーザーに確認

- [ ] 10. ビュー層（Thymeleaf）の実装
  - [ ] 10.1 login.htmlを作成
    - ログインフォーム（ユーザー名、パスワード）
    - エラーメッセージ表示（th:if="${param.error}"）
    - ログアウトメッセージ表示（th:if="${param.logout}"）
    - 新規ユーザー登録リンク
    - _要件: 11.1, 11.3_
  
  - [ ] 10.2 register.htmlを作成
    - 登録フォーム（ユーザー名、パスワード、従業員ID）
    - エラーメッセージ表示（th:if="${errorMessage}"）
    - ログインページへのリンク
    - _要件: 12.1, 12.3, 12.4_
  
  - [ ] 10.3 home.htmlを作成
    - ログイン中の従業員ID表示
    - 成功メッセージ表示（th:if="${successMessage}"）
    - エラーメッセージ表示（th:if="${errorMessage}"）
    - 出勤ボタン（POST /attendance/clock-in）
    - 退勤ボタン（POST /attendance/clock-out）
    - 勤怠履歴リンク
    - ログアウトフォーム
    - _要件: 1.3, 2.3, 10.1, 10.2, 10.3, 14.2_
  
  - [ ] 10.4 history.htmlを作成
    - 勤怠レコードテーブル（日付、出勤時刻、退勤時刻、勤務時間）
    - 勤怠レコードがない場合のメッセージ（th:if="${#lists.isEmpty(records)}"）
    - 退勤時刻がnullの場合は「未退勤」と表示
    - ホームへのリンク
    - ログアウトフォーム
    - _要件: 3.1, 3.2, 3.3, 3.4, 4.3, 14.2_

- [ ] 11. 勤務時間フォーマット機能の実装
  - [ ] 11.1 勤務時間を「X時間Y分」形式でフォーマットするヘルパーメソッドを実装
    - AttendanceControllerまたはユーティリティクラスに実装
    - Durationを受け取り、文字列を返す
    - _要件: 4.3_
  
  - [ ]* 11.2 勤務時間フォーマットのプロパティテストを作成
    - **プロパティ9: 勤務時間の表示フォーマット**
    - **検証: 要件 4.3**

- [ ] 12. 統合テストの実装
  - [ ]* 12.1 出勤から退勤までの完全なフローの統合テストを作成
    - @SpringBootTest、@AutoConfigureTestDatabaseを使用
    - ユーザー登録、ログイン、出勤、退勤、履歴取得の一連の流れをテスト
    - _要件: 1.1, 2.1, 3.1, 11.2, 12.2, 15.1, 15.2, 15.3_
  
  - [ ]* 12.2 セキュリティ統合テストを作成
    - 認証なしでのアクセス拒否テスト
    - ログイン成功後のアクセス許可テスト
    - ログアウト後のアクセス拒否テスト
    - _要件: 11.4, 14.2, 14.3_

- [ ] 13. 最終チェックポイント - すべてのテストを実行
  - すべてのユニットテスト、プロパティテスト、統合テストが成功することを確認
  - アプリケーションを起動し、基本的な動作を確認
  - 質問があればユーザーに確認

## 注意事項

- `*`マークが付いたタスクはオプションであり、より早いMVPのためにスキップ可能です
- 各タスクは特定の要件を参照しており、トレーサビリティを確保しています
- チェックポイントでは段階的な検証を行い、問題を早期に発見します
- プロパティテストは最低100回のイテレーションで実行してください
- ユニットテストとプロパティテストは補完的であり、両方が包括的なカバレッジに必要です

