import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("sementic version -> version code 변환 코드 테스트")
class SemVerTest {
    @Test
    @DisplayName("기본 형식의 버전이 올바르게 변환되어야 한다")
    fun semVerTest1() {
        assert(
            SemVer.sementicVersionToSerializedCode("3.9.2-rc.1").toInt()
                == 2013090201
        )
    }

    @Test
    @DisplayName("두 자리의 rc 버전이 올바르게 변환되어야 한다")
    fun semVerTest2() {
        assert(
            SemVer.sementicVersionToSerializedCode("4.3.8-rc.11").toInt()
                == 2014030811
        )
    }

    @Test
    @DisplayName("rc가 없는 버전은 해당 마이너 버전의 최대인 99로 처리되어야 한다")
    fun semVerTest3() {
        assert(
            SemVer.sementicVersionToSerializedCode("3.9.2").toInt()
                == 2013090299
        )
    }

    @Test
    @DisplayName("두 자리의 마이너 버전이 올바르게 변환되어야 한다")
    fun semVerTest4() {
        assert(
            SemVer.sementicVersionToSerializedCode("3.10.1-rc.1").toInt()
                == 2013100101
        )
    }

    @Test
    @DisplayName("두 자리의 메이저 버전이 올바르게 변환되어야 하며, 앞에서 세 번째 자리에 반영된다")
    fun semVerTest5() {
        assert(
            SemVer.sementicVersionToSerializedCode("10.0.0-rc.1").toInt()
                == 2020000001
        )
    }
}
