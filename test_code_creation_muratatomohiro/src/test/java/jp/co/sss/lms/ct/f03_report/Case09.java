package jp.co.sss.lms.ct.f03_report;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト レポート機能
 * ケース09
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース09 受講生 レポート登録 入力チェック")
public class Case09 {

	/** 前処理 */
	@BeforeAll
	static void before() {
		createDriver();
	}

	/** 後処理 */
	@AfterAll
	static void after() {
		closeDriver();
	}

	@Test
	@Order(1)
	@DisplayName("テスト01 トップページURLでアクセス")
	void test01() {
		goTo("http://localhost:8080/lms/");

		getEvidence(new Object() {
		}, "ケース09_No.01");

		assertEquals(webDriver.getTitle(), "ログイン | LMS");
		assertEquals(webDriver.findElement(By.className("btn")).getAttribute("value"), "ログイン");
		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/");
	}

	@Test
	@Order(2)
	@DisplayName("テスト02 初回ログイン済みの受講生ユーザーでログイン")
	void test02() {
		// ログインに必要な要素の取得
		WebElement userID = WebDriverUtils.webDriver.findElement(By.id("loginId"));
		WebElement password = WebDriverUtils.webDriver.findElement(By.id("password"));
		WebElement loginButton = WebDriverUtils.webDriver.findElement(By.cssSelector("[value='ログイン']"));

		userID.clear();
		userID.sendKeys("StudentAA01");
		password.clear();
		password.sendKeys("Password01");

		getEvidence(new Object() {
		}, "ケース09_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース09_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 上部メニューの「ようこそ○○さん」リンクからユーザー詳細画面に遷移")
	void test03() {
		WebElement linkElement = webDriver.findElement(By.xpath("//small[contains(text(),'ようこそ')]"));

		getEvidence(new Object() {
		}, "ケース09_No.03_1");

		linkElement.click();

		visibilityTimeout(By.xpath("//h2[text() = 'ユーザー詳細']"), 10);

		getEvidence(new Object() {
		}, "ケース09_No.03_2");
	}

	@Test
	@Order(4)
	@DisplayName("テスト04 該当レポートの「修正する」ボタンを押下しレポート登録画面に遷移")
	void test04() {
		// 週報提出済みの日付要素のテーブルデータを取得
		WebElement days = webDriver.findElement(By.xpath("//td[text() = '2022年10月2日(日)']"));
		// 取得したテーブルデータの親要素(テーブルレコード)を取得
		WebElement section = days.findElement(By.xpath(".."));
		// 該当のテーブルレコード要素内から詳細ボタン要素を取得
		WebElement registButton = section.findElement(By.xpath(".//form[@action='/lms/report/regist']"));

		scrollByElementAndOffset(registButton, "-50");

		getEvidence(new Object() {
		}, "ケース09_No.04_1");

		registButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		assertEquals(webDriver.getTitle(), "レポート登録 | LMS");
		getEvidence(new Object() {
		}, "ケース09_No.04_2");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 報告内容を修正して「提出する」ボタンを押下しエラー表示：学習項目が未入力")
	void test05() {
		WebElement learningTopic = webDriver.findElement(By.xpath("//label[contains(text(),'学習項目')]"));
		WebElement inputForm = learningTopic.findElement(By.xpath("//input[@type='text']"));
		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));
		String inputtedText = inputForm.getAttribute("value");

		scrollByElementAndOffset(learningTopic, "-100");

		getEvidence(new Object() {
		}, "ケース09_No.05_1");

		inputForm.clear();

		getEvidence(new Object() {
		}, "ケース09_No.05_2");

		scrollByElement(submissionButton);
		submissionButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		// 画面更新が入るので再度要素の取得
		learningTopic = webDriver.findElement(By.xpath("//label[contains(text(),'学習項目')]"));
		inputForm = learningTopic.findElement(By.xpath("//input[@type='text']"));
		WebElement formGroup = learningTopic.findElement(By.xpath(".."));
		WebElement errorMsg = formGroup.findElement(By.xpath(".//p[@class='help-inline error']"));

		scrollByElementAndOffset(learningTopic, "-100");

		assertEquals(errorMsg.getText(), "* 理解度を入力した場合は、学習項目は必須です。");
		getEvidence(new Object() {
		}, "ケース09_No.05_3");

		// 入力されていた文字列に戻す
		inputForm.sendKeys(inputtedText);
	}

	@Test
	@Order(6)
	@DisplayName("テスト06 不適切な内容で修正して「提出する」ボタンを押下しエラー表示：理解度が未入力")
	void test06() {
		WebElement learningTopic = webDriver.findElement(By.xpath("//label[contains(text(),'学習項目')]"));
		Select selectBox = new Select(learningTopic.findElement(By.xpath("//select[@id='intFieldValue_0']")));
		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));

		scrollByElementAndOffset(learningTopic, "-100");

		getEvidence(new Object() {
		}, "ケース09_No.06_1");

		// 未選択状態にする
		selectBox.selectByIndex(0);

		getEvidence(new Object() {
		}, "ケース09_No.06_2");

		scrollByElement(submissionButton);
		submissionButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		// 画面更新が入るので再度要素の取得
		learningTopic = webDriver.findElement(By.xpath("//label[contains(text(),'学習項目')]"));
		selectBox = new Select(learningTopic.findElement(By.xpath("//select[@id='intFieldValue_0']")));
		WebElement formGroup = learningTopic.findElement(By.xpath(".."));
		WebElement errorMsg = formGroup.findElement(By.xpath(".//p[@class='help-inline error']"));

		scrollByElementAndOffset(learningTopic, "-100");

		assertEquals(errorMsg.getText(), "* 学習項目を入力した場合は、理解度は必須です。");
		getEvidence(new Object() {
		}, "ケース09_No.06_3");
	}

	@Test
	@Order(7)
	@DisplayName("テスト07 不適切な内容で修正して「提出する」ボタンを押下しエラー表示：目標の達成度が数値以外")
	void test07() {
		WebElement targetLabel = webDriver.findElement(By.xpath("//label[contains(text(),'目標の達成度')]"));
		WebElement formGroup = targetLabel.findElement(By.xpath(".."));
		WebElement inputForm = formGroup.findElement(By.tagName("textarea"));
		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));
		String inputtedText = inputForm.getAttribute("value");

		scrollByElementAndOffset(targetLabel, "-50");

		getEvidence(new Object() {
		}, "ケース09_No.07_1");

		inputForm.clear();

		getEvidence(new Object() {
		}, "ケース09_No.07_2");

		// [目標の達成度]欄に、「ABC」を入力
		inputForm.sendKeys("ABC");

		getEvidence(new Object() {
		}, "ケース09_No.07_3");

		scrollByElement(submissionButton);
		submissionButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		// 画面更新が入るので再度要素の取得
		targetLabel = webDriver.findElement(By.xpath("//label[contains(text(),'目標の達成度')]"));
		formGroup = targetLabel.findElement(By.xpath(".."));
		inputForm = formGroup.findElement(By.tagName("textarea"));

		scrollByElementAndOffset(targetLabel, "-50");

		WebElement errorMsg = formGroup.findElement(By.xpath(".//p[@class='help-inline error']"));
		assertEquals(errorMsg.getText(), "* 目標の達成度は半角数字で入力してください。");
		getEvidence(new Object() {
		}, "ケース09_No.07_4");

		inputForm.clear();
		inputForm.sendKeys(inputtedText);
	}

	@Test
	@Order(8)
	@DisplayName("テスト08 不適切な内容で修正して「提出する」ボタンを押下しエラー表示：目標の達成度が範囲外")
	void test08() {
		WebElement targetLabel = webDriver.findElement(By.xpath("//label[contains(text(),'目標の達成度')]"));
		WebElement formGroup = targetLabel.findElement(By.xpath(".."));
		WebElement inputForm = formGroup.findElement(By.tagName("textarea"));
		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));
		String inputtedText = inputForm.getAttribute("value");

		scrollByElementAndOffset(targetLabel, "-50");

		getEvidence(new Object() {
		}, "ケース09_No.08_1");

		inputForm.clear();

		getEvidence(new Object() {
		}, "ケース09_No.08_2");

		// [目標の達成度]欄に、「100」を入力
		inputForm.sendKeys("100");

		getEvidence(new Object() {
		}, "ケース09_No.08_3");

		scrollByElement(submissionButton);
		submissionButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		// 画面更新が入るので再度要素の取得
		targetLabel = webDriver.findElement(By.xpath("//label[contains(text(),'目標の達成度')]"));
		formGroup = targetLabel.findElement(By.xpath(".."));
		inputForm = formGroup.findElement(By.tagName("textarea"));

		scrollByElementAndOffset(targetLabel, "-50");

		WebElement errorMsg = formGroup.findElement(By.xpath(".//p[@class='help-inline error']"));
		assertEquals(errorMsg.getText(), "* 目標の達成度は、半角数字で、1～10の範囲内で入力してください。");
		getEvidence(new Object() {
		}, "ケース09_No.08_4");

		inputForm.clear();
		inputForm.sendKeys(inputtedText);
	}

	@Test
	@Order(9)
	@DisplayName("テスト09 不適切な内容で修正して「提出する」ボタンを押下しエラー表示：目標の達成度・所感が未入力")
	void test09() {
		// [目標の達成度]要素
		WebElement goalLabel = webDriver.findElement(By.xpath("//label[contains(text(),'目標の達成度')]"));
		WebElement goalFormGroup = goalLabel.findElement(By.xpath(".."));
		WebElement goalInputForm = goalFormGroup.findElement(By.tagName("textarea"));

		// [所感]要素
		WebElement impressLabel = webDriver.findElement(By.xpath("//label[contains(text(),'所感')]"));
		WebElement impressFormGroup = impressLabel.findElement(By.xpath(".."));
		WebElement impressInputForm = impressFormGroup.findElement(By.tagName("textarea"));

		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));

		String goalInputtedTextString = goalInputForm.getAttribute("value");
		String impressInputtedTextString = impressInputForm.getAttribute("value");

		scrollByElementAndOffset(goalLabel, "-50");
		getEvidence(new Object() {
		}, "ケース09_No.09_1");

		goalInputForm.clear();
		impressInputForm.clear();

		getEvidence(new Object() {
		}, "ケース09_No.09_2");

		scrollByElement(submissionButton);
		submissionButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		// [目標の達成度]要素の再取得
		goalLabel = webDriver.findElement(By.xpath("//label[contains(text(),'目標の達成度')]"));
		goalFormGroup = goalLabel.findElement(By.xpath(".."));
		goalInputForm = goalFormGroup.findElement(By.tagName("textarea"));

		// [所感]要素の再取得
		impressLabel = webDriver.findElement(By.xpath("//label[contains(text(),'所感')]"));
		impressFormGroup = impressLabel.findElement(By.xpath(".."));
		impressInputForm = impressFormGroup.findElement(By.tagName("textarea"));

		scrollByElementAndOffset(goalLabel, "-50");

		WebElement goalErrorMsg = goalFormGroup.findElement(By.xpath(".//p[@class='help-inline error']"));
		assertEquals(goalErrorMsg.getText(), "* 目標の達成度は半角数字で入力してください。");

		WebElement impressErrorMsg = impressFormGroup.findElement(By.xpath(".//p[@class='help-inline error']"));
		assertEquals(impressErrorMsg.getText(), "* 所感は必須です。");

		getEvidence(new Object() {
		}, "ケース09_No.09_3");

		goalInputForm.sendKeys(goalInputtedTextString);
		impressInputForm.sendKeys(impressInputtedTextString);

	}

	@Test
	@Order(10)
	@DisplayName("テスト10 不適切な内容で修正して「提出する」ボタンを押下しエラー表示：所感・一週間の振り返りが2000文字超")
	void test10() {
		// [所感]要素
		WebElement impressLabel = webDriver.findElement(By.xpath("//label[contains(text(),'所感')]"));
		WebElement impressFormGroup = impressLabel.findElement(By.xpath(".."));
		WebElement impressInputForm = impressFormGroup.findElement(By.tagName("textarea"));

		// [一週間の振り返り]要素
		WebElement reflectionLabel = webDriver.findElement(By.xpath("//label[contains(text(),'一週間の振り返り')]"));
		WebElement reflectionFormGroup = reflectionLabel.findElement(By.xpath(".."));
		WebElement reflectionInputForm = reflectionFormGroup.findElement(By.tagName("textarea"));

		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));

		scrollByElementAndOffset(impressLabel, "-50");
		getEvidence(new Object() {
		}, "ケース09_No.10_1");

		impressInputForm.clear();
		reflectionInputForm.clear();
		getEvidence(new Object() {
		}, "ケース09_No.10_2");

		// [所感]欄に「a」を2001文字入力
		impressInputForm.sendKeys("a".repeat(2001));
		// [一週間の振り返り]欄に「a」を2001文字入力
		reflectionInputForm.sendKeys("a".repeat(2001));
		getEvidence(new Object() {
		}, "ケース09_No.10_3");

		scrollByElement(submissionButton);
		submissionButton.click();

		visibilityTimeout(By.xpath("//h2[contains(text(),'週報')]"), 10);

		// [所感]要素の再取得
		impressLabel = webDriver.findElement(By.xpath("//label[contains(text(),'所感')]"));
		impressFormGroup = impressLabel.findElement(By.xpath(".."));

		// [一週間の振り返り]要素の再取得
		reflectionLabel = webDriver.findElement(By.xpath("//label[contains(text(),'一週間の振り返り')]"));
		reflectionFormGroup = reflectionLabel.findElement(By.xpath(".."));

		scrollByElementAndOffset(impressLabel, "-50");

		WebElement goalErrorMsg = impressFormGroup.findElement(By.xpath(".//p[@class='help-inline error']"));
		assertEquals(goalErrorMsg.getText(), "* 所感の長さが最大値(2000)を超えています。");

		WebElement impressErrorMsg = reflectionFormGroup.findElement(By.xpath(".//p[@class='help-inline error']"));
		assertEquals(impressErrorMsg.getText(), "* 一週間の振り返りの長さが最大値(2000)を超えています。");

		getEvidence(new Object() {
		}, "ケース09_No.10_4");

	}

}
