import org.testng.annotations.DataProvider;

public class SearchProvider {

    @DataProvider(name = "query-parameters")
    public Object[][] queryParameters() {
        return new Object[][]{
                new Object[]{"Multiple words string", "FC Barcelona"},
                new Object[]{"String with special characters", "Tom&Jerry"},
                new Object[]{"String with numbers", "123"}};
    }

    @DataProvider(name = "language-parameters")
    public Object[][] languageParameters() {
        return new Object[][]{
                new Object[]{"Кот", "ru", false},
                new Object[]{"gato", "es", false},
                new Object[]{"Кот", "es", true},
                new Object[]{"Кот", "", true}};
    }
}
