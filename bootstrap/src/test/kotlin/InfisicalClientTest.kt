import com.challa.bootstrap.secret.InfisicalClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InfisicalClientTest {

    @Test
    fun `Infisical 연동 확인`() {
        val client = InfisicalClient(
            clientId = System.getenv("INFISICAL_CLIENT_ID"),
            clientSecret = System.getenv("INFISICAL_CLIENT_SECRET")
        )

        val secrets = client.loadSecrets(
            projectId = System.getenv("INFISICAL_PROJECT_ID"),
            environment = "dev"
        )

        assertEquals(
            "HappyHouse",
            secrets["TEST_SECRET"]
        )
    }
}
