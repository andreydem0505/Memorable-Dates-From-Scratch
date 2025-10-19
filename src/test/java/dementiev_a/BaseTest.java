package dementiev_a;

import dementiev_a.data.manager.PostgresManager;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    @BeforeAll
    static void init() {
        PostgresManager.setDbName("memorable_dates_test");
    }
}
