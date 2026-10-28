# es2-pc09

Testes automatizados do [OrangeHRM demo](https://opensource-demo.orangehrmlive.com/) com Playwright em Java, JUnit 5 e Page Object.

Funcionalidades testadas:

- Login
- Recruitment > Vacancies (adicionar, editar, pesquisar e deletar)

Os cenários estão em [cenarios.md](cenarios.md).

## Estrutura

```
src/test/java/
  pages/   page objects (LoginPage, DashboardPage, VacanciesPage, VacancyFormPage)
  tests/   BaseTest, LoginTest e VacancyTest
```

## Como rodar

Precisa de Java 21 e Maven.

```bash
mvn test
```

Para rodar só uma classe:

```bash
mvn test -Dtest=LoginTest
```

Para ver o navegador abrindo:

```bash
mvn test -Dheadless=false
```

Na primeira vez o Playwright baixa o navegador sozinho. Os testes demoram alguns minutos porque o site demo é público e às vezes fica lento.
