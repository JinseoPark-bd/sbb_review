package com.sbb.in;

import com.sbb.in.answer.Answer;
import com.sbb.in.answer.AnswerRepository;
import com.sbb.in.question.Question;
import com.sbb.in.question.QuestionRepository;
import com.sbb.in.question.QuestionService;
import org.aspectj.weaver.loadtime.Options;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private QuestionService questionService;

	@Test
	@DisplayName("데이터 저장")
	void t001() {
		Question q1 = new Question();
		q1.setSubject("sbb가 무엇인가요?");
		q1.setContent("sbb에 대해서 알고 싶습니다.");
		q1.setCreateDate(LocalDateTime.now());
		questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("스프링부트 모델 질문입니다.");
		q2.setContent("id는 자동으로 생성되나요?");
		q2.setCreateDate(LocalDateTime.now());
		questionRepository.save(q2);
	}

	@Test
	@DisplayName("findAll")
	void t002() {
		List<Question> all = this.questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());

	}

	@Test
	@DisplayName("findById")
	void t003() {
		/*
		optional은 null처리를 유연하게 처리하기 위해 사용하는 클래스로
		isPresent로 null이 아닌지 확인 후 get으로 실제 객체 값을 얻어야 한다.
		 */
		Optional<Question> oq = this.questionRepository.findById(1);
		if (oq.isPresent()) {
			Question q = oq.get();
			assertEquals("sbb가 무엇인가요?", q.getSubject());
		}
	}

	@Test
	@DisplayName("findBySubject")
	void t004() {
		Question q = questionRepository.findBySubject("sbb가 무엇인가요?");
		assertEquals(1, q.getId());
	}

	@Test
	@DisplayName("findBySubjectAndContent")
	void t005() {
		Question q = questionRepository.findBySubjectAndContent(
				"sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."
		);
		assertEquals(1, q.getId());

	}

	@Test
	@DisplayName("findBySubjectLike")
	void t006() {
		List<Question> questionList = questionRepository.findBySubjectLike("sbb%");
		Question q = questionList.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());
	}

	@Test
	@DisplayName("데이터 수정")
	void t007() {
		Optional<Question> oq = questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		q.setSubject("수정된 제목");
		questionRepository.save(q);
	}

	@Test
	@DisplayName("데이터 삭제")
	void t008() {
		assertEquals(2, questionRepository.count());
		Optional<Question> oq = questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		questionRepository.delete(q);
		assertEquals(1, questionRepository.count());
	}

	@Test
	@DisplayName("답변 데이터 생성 후 저장")
	void t009() {
		Optional<Question> oq = questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("네 자동으로 생성됩니다.");
		a.setQuestion(q);
		a.setCreateDate(LocalDateTime.now());
		answerRepository.save(a);

		Answer b = new Answer();
		b.setContent("따로 생성할 필요가 없습니다.");
		b.setQuestion(q);
		b.setCreateDate(LocalDateTime.now());
		answerRepository.save(b);
	}

	@Test
	@DisplayName("답변 조회")
	void t010() {
		Optional<Answer> oa = answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer a = new Answer();
		assertEquals(2, a.getQuestion().getId());
	}

	@Transactional
	@Test
	@DisplayName("질문에 달린 답변 찾기")
	void t011() {
		Optional<Question> oq = questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		List<Answer> answerList = q.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
	}

	@Test
	@DisplayName("대량 데이터 넣기")
	void t012() {
		for(int i = 1; i <=300; i++) {
			String subject = String.format("테스트 데이터입니다:[%03d]",i);
			String content = "내용무";
			questionService.create(subject, content);
		}
	}
}
