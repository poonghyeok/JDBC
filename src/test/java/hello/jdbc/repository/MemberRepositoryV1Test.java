package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPoolName(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException, InterruptedException {
        //save
        Member member = new Member("m_deltest", 2000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember); //Member 객체의 @Data annotation이 toString()을 overriding 하기 때문에 객체의 주소값이 나오지 않고 class 명과 필드 값을 출력하게 된다.
        assertThat(findMember).isEqualTo(member); //findMember 랑 member랑 다른 인스턴스인데 어떻게 assertThat에서 아무것도 안터질 수가 있는 지, 이 또한 lombok의 data를 쓰게 되면 EqualsAndHashCode를 overriding하면서 모든 필드 값을 비교해서 equals를 만들어준다.

        //update : memberV2 money 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class); //NoSuchException이 터지는 지 확인, findById 메서드에서 exception을 throw 했기 떄문에 Assertions의 method명도 thrownBy 어떤 에외 클래스에 의해서 예외가 던져지는지 assert하는 검증 과정인가 보다.

        Thread.sleep(10000);
    }
}