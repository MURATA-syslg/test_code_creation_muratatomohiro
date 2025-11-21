package jp.co.sss.lms.ct.f02_faq;

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

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト よくある質問機能
 * ケース04
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース04 よくある質問画面への遷移")
public class Case04 {

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
		}, "ケース04_No.01");

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
		}, "ケース04_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース04_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 上部メニューの「ヘルプ」リンクからヘルプ画面に遷移")
	void test03() {
		WebElement dropDownToggle = webDriver.findElement(By.className("dropdown-toggle"));
		dropDownToggle.click();

		visibilityTimeout(By.linkText("ヘルプ"), 10);
		getEvidence(new Object() {
		}, "ケース04_No.03_1");

		WebElement dropMenu = webDriver.findElement(By.linkText("ヘルプ"));
		dropMenu.click();

		visibilityTimeout(By.xpath("//h2[text() = 'ヘルプ']"), 10);
		getEvidence(new Object() {
		}, "ケース04_No.03_2");

		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/help");
		assertEquals(webDriver.getTitle(), "ヘルプ | LMS");
	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「よくある質問」リンクからよくある質問画面を別タブに開く")
	void test04() {
		WebElement faq = webDriver.findElement(By.linkText("よくある質問"));
		getEvidence(new Object() {
		}, "ケース04_No.04_1");
		faq.click();

		// ウィンドウハンドルを取得する
		Object[] windowHandles = webDriver.getWindowHandles().toArray();

		// seleniumで操作可能なdriverを切り替える
		webDriver.switchTo().window((String) windowHandles[1]);

		visibilityTimeout(By.xpath("//h2[text() = 'よくある質問']"), 10);
		getEvidence(new Object() {
		}, "ケース04_No.04_2");

		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/faq");
		assertEquals(webDriver.getTitle(), "よくある質問 | LMS");

	}

}
