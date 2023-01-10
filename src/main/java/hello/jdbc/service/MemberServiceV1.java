package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember  = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validate(toMember); //여기서 예외를 터뜨린다면 다음 update 쿼리는 실행되지 않을 것이다..! 트랜잭션을 사용하지 않는다면, 같은 작업인데 하나만 update 되고 다른 하나는 update 되지 않는 심각한 오류 발생..!

        memberRepository.update(toId, toMember.getMoney() + money);

    }

    private void validate(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
