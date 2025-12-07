// SistemaGestaoEducacional.java
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// ==================== PACOTE: DOMÍNIO (CORE BUSINESS) ====================
// Nenhum System.out.println aqui - apenas lógica de negócio pura

// Interface para logging (Strategy Pattern)
interface Logger {
    void info(String mensagem);
    void erro(String mensagem);
    void sucesso(String mensagem);
    void debug(String mensagem);
    void titulo(String mensagem);
}

// Interface para output de UI
interface OutputHandler {
    void mostrarMenu(String menu);
    void mostrarMensagem(String mensagem);
    void mostrarErro(String erro);
    void mostrarSucesso(String sucesso);
    void limparTela();
    void mostrarSeparador();
}

// Interface para entrada de dados
interface InputHandler {
    String lerString(String prompt);
    int lerInt(String prompt);
    double lerDouble(String prompt);
    boolean lerBoolean(String prompt, String mensagemSim, String mensagemNao);
    int lerOpcao(String prompt, int min, int max);
}

// ==================== MODELOS DE DOMÍNIO ====================
class Aluno {
    private final String nome;
    private final String matricula;
    private final String curso;
    
    public Aluno(String nome, String matricula, String curso) {
        this.nome = nome;
        this.matricula = matricula;
        this.curso = curso;
    }
    
    public String getNome() { return nome; }
    public String getMatricula() { return matricula; }
    public String getCurso() { return curso; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aluno aluno = (Aluno) obj;
        return matricula.equals(aluno.matricula);
    }
    
    @Override
    public int hashCode() {
        return matricula.hashCode();
    }
}

class Professor {
    private final String nome;
    private final String especialidade;
    private final String registro;
    
    public Professor(String nome, String especialidade, String registro) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.registro = registro;
    }
    
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public String getRegistro() { return registro; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Professor professor = (Professor) obj;
        return registro.equals(professor.registro);
    }
    
    @Override
    public int hashCode() {
        return registro.hashCode();
    }
}

class Curso {
    protected final String nome;
    protected final String codigo;
    protected final int cargaHoraria;
    
    public Curso(String nome, String codigo, int cargaHoraria) {
        this.nome = nome;
        this.codigo = codigo;
        this.cargaHoraria = cargaHoraria;
    }
    
    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
    public int getCargaHoraria() { return cargaHoraria; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Curso curso = (Curso) obj;
        return codigo.equals(curso.codigo);
    }
    
    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}

class Turma {
    private final String codigo;
    private final Professor professor;
    private final Curso curso;
    private final List<Aluno> alunos;
    
    public Turma(String codigo, Professor professor, Curso curso) {
        this.codigo = codigo;
        this.professor = professor;
        this.curso = curso;
        this.alunos = new ArrayList<>();
    }
    
    public void adicionarAluno(Aluno aluno) {
        if (!alunos.contains(aluno)) {
            alunos.add(aluno);
        }
    }
    
    public void removerAluno(Aluno aluno) {
        alunos.remove(aluno);
    }
    
    public String getCodigo() { return codigo; }
    public Professor getProfessor() { return professor; }
    public Curso getCurso() { return curso; }
    public List<Aluno> getAlunos() { return new ArrayList<>(alunos); }
    public int getQuantidadeAlunos() { return alunos.size(); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Turma turma = (Turma) obj;
        return codigo.equals(turma.codigo);
    }
    
    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}

// ==================== AVALIAÇÕES E NOTAS ====================
class Avaliacao {
    private double nota;
    private final String descricao;
    private final String tipo; // "Prova", "Trabalho", "Seminário", etc.
    private final double peso; // Peso da avaliação (0.0 a 1.0)
    
    public Avaliacao(String descricao, String tipo, double peso) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.peso = peso;
        this.nota = 0.0;
    }
    
    public void atribuirNota(double valor) throws IllegalArgumentException {
        if (valor < 0 || valor > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        this.nota = valor;
    }
    
    public double getNota() { return nota; }
    public String getDescricao() { return descricao; }
    public String getTipo() { return tipo; }
    public double getPeso() { return peso; }
    public double getNotaPonderada() { return nota * peso; }
}

class AvaliacaoTurma {
    private final Turma turma;
    private final List<AvaliacaoAluno> avaliacoesAlunos;
    
    public AvaliacaoTurma(Turma turma) {
        this.turma = turma;
        this.avaliacoesAlunos = new ArrayList<>();
    }
    
    public void adicionarAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno) {
        avaliacoesAlunos.add(avaliacaoAluno);
    }
    
    public List<AvaliacaoAluno> getAvaliacoesAlunos() {
        return new ArrayList<>(avaliacoesAlunos);
    }
    
    public Turma getTurma() { return turma; }
    
    public double calcularMediaTurma() {
        if (avaliacoesAlunos.isEmpty()) return 0.0;
        
        double soma = 0.0;
        for (AvaliacaoAluno aa : avaliacoesAlunos) {
            soma += aa.calcularMedia();
        }
        return soma / avaliacoesAlunos.size();
    }
    
    public Aluno getMelhorAluno() {
        if (avaliacoesAlunos.isEmpty()) return null;
        
        AvaliacaoAluno melhor = avaliacoesAlunos.get(0);
        for (AvaliacaoAluno aa : avaliacoesAlunos) {
            if (aa.calcularMedia() > melhor.calcularMedia()) {
                melhor = aa;
            }
        }
        return melhor.getAluno();
    }
}

class AvaliacaoAluno {
    private final Aluno aluno;
    private final List<Avaliacao> avaliacoes;
    
    public AvaliacaoAluno(Aluno aluno) {
        this.aluno = aluno;
        this.avaliacoes = new ArrayList<>();
    }
    
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }
    
    public Aluno getAluno() { return aluno; }
    public List<Avaliacao> getAvaliacoes() { return new ArrayList<>(avaliacoes); }
    
    public double calcularMedia() {
        if (avaliacoes.isEmpty()) return 0.0;
        
        double somaNotasPonderadas = 0.0;
        double somaPesos = 0.0;
        
        for (Avaliacao a : avaliacoes) {
            somaNotasPonderadas += a.getNotaPonderada();
            somaPesos += a.getPeso();
        }
        
        return somaPesos > 0 ? somaNotasPonderadas / somaPesos : 0.0;
    }
    
    public String getStatus() {
        double media = calcularMedia();
        if (media >= 7.0) return "APROVADO";
        else if (media >= 5.0) return "RECUPERAÇÃO";
        else return "REPROVADO";
    }
}

// ==================== HERANÇA E POLIMORFISMO ====================
class CursoPresencial extends Curso {
    private final String sala;
    
    public CursoPresencial(String nome, String codigo, int cargaHoraria, String sala) {
        super(nome, codigo, cargaHoraria);
        this.sala = sala;
    }
    
    public String getSala() { return sala; }
    
    @Override
    public String toString() {
        return String.format("Curso Presencial: %s | Código: %s | Carga: %dh | Sala: %s",
                nome, codigo, cargaHoraria, sala);
    }
}

class CursoEAD extends Curso {
    private final String plataforma;
    
    public CursoEAD(String nome, String codigo, int cargaHoraria, String plataforma) {
        super(nome, codigo, cargaHoraria);
        this.plataforma = plataforma;
    }
    
    public String getPlataforma() { return plataforma; }
    
    @Override
    public String toString() {
        return String.format("Curso EAD: %s | Código: %s | Carga: %dh | Plataforma: %s",
                nome, codigo, cargaHoraria, plataforma);
    }
}

// ==================== INTERFACES E ABSTRAÇÃO ====================
interface Autenticavel {
    ResultadoAutenticacao autenticar(Credenciais credenciais);
}

class Credenciais {
    private final String login;
    private final String senha;
    
    public Credenciais(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }
    
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
}

class ResultadoAutenticacao {
    private final boolean sucesso;
    private final String mensagem;
    private final Usuario usuario;
    
    public ResultadoAutenticacao(boolean sucesso, String mensagem, Usuario usuario) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.usuario = usuario;
    }
    
    public boolean isSucesso() { return sucesso; }
    public String getMensagem() { return mensagem; }
    public Usuario getUsuario() { return usuario; }
}

abstract class Usuario {
    protected final String nome;
    protected final String email;
    
    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }
    
    public abstract String getTipo();
    
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}

class AlunoAutenticavel extends Usuario implements Autenticavel {
    private final String matricula;
    private final String login;
    private final String senhaHash;
    private final List<Avaliacao> avaliacoes;
    
    public AlunoAutenticavel(String nome, String email, String matricula, 
                           String login, String senha) {
        super(nome, email);
        this.matricula = matricula;
        this.login = login;
        this.senhaHash = hashSenha(senha);
        this.avaliacoes = new ArrayList<>();
    }
    
    @Override
    public ResultadoAutenticacao autenticar(Credenciais credenciais) {
        boolean autenticado = this.login.equals(credenciais.getLogin()) 
                            && verificarSenha(credenciais.getSenha());
        
        String mensagem = autenticado 
            ? String.format("Aluno %s autenticado com sucesso", nome)
            : "Falha na autenticação: login ou senha incorretos";
        
        return new ResultadoAutenticacao(autenticado, mensagem, this);
    }
    
    @Override
    public String getTipo() {
        return "ALUNO";
    }
    
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }
    
    public List<Avaliacao> getAvaliacoes() {
        return new ArrayList<>(avaliacoes);
    }
    
    public String getMatricula() {
        return matricula;
    }
    
    public double calcularMedia() {
        if (avaliacoes.isEmpty()) return 0.0;
        
        double somaNotasPonderadas = 0.0;
        double somaPesos = 0.0;
        
        for (Avaliacao a : avaliacoes) {
            somaNotasPonderadas += a.getNotaPonderada();
            somaPesos += a.getPeso();
        }
        
        return somaPesos > 0 ? somaNotasPonderadas / somaPesos : 0.0;
    }
    
    public String getStatus() {
        double media = calcularMedia();
        if (media >= 7.0) return "APROVADO";
        else if (media >= 5.0) return "RECUPERAÇÃO";
        else return "REPROVADO";
    }
    
    private String hashSenha(String senha) {
        return Integer.toString(senha.hashCode());
    }
    
    private boolean verificarSenha(String senha) {
        return senhaHash.equals(Integer.toString(senha.hashCode()));
    }
}

class ProfessorAutenticavel extends Usuario implements Autenticavel {
    private final String especialidade;
    private final String registro;
    private final String login;
    private final String senhaHash;
    
    public ProfessorAutenticavel(String nome, String email, String especialidade,
                               String registro, String login, String senha) {
        super(nome, email);
        this.especialidade = especialidade;
        this.registro = registro;
        this.login = login;
        this.senhaHash = hashSenha(senha);
    }
    
    @Override
    public ResultadoAutenticacao autenticar(Credenciais credenciais) {
        boolean autenticado = this.login.equals(credenciais.getLogin())
                            && verificarSenha(credenciais.getSenha());
        
        String mensagem = autenticado
            ? String.format("Professor %s autenticado com sucesso", nome)
            : "Falha na autenticação: login ou senha incorretos";
        
        return new ResultadoAutenticacao(autenticado, mensagem, this);
    }
    
    @Override
    public String getTipo() {
        return "PROFESSOR";
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public String getRegistro() {
        return registro;
    }
    
    private String hashSenha(String senha) {
        return Integer.toString(senha.hashCode());
    }
    
    private boolean verificarSenha(String senha) {
        return senhaHash.equals(Integer.toString(senha.hashCode()));
    }
}

class Administrador extends Usuario implements Autenticavel {
    private final String login;
    private final String senhaHash;
    
    public Administrador(String nome, String email, String login, String senha) {
        super(nome, email);
        this.login = login;
        this.senhaHash = hashSenha(senha);
    }
    
    @Override
    public ResultadoAutenticacao autenticar(Credenciais credenciais) {
        boolean autenticado = this.login.equals(credenciais.getLogin())
                            && verificarSenha(credenciais.getSenha());
        
        String mensagem = autenticado
            ? String.format("Administrador %s autenticado com sucesso", nome)
            : "Falha na autenticação: login ou senha incorretos";
        
        return new ResultadoAutenticacao(autenticado, mensagem, this);
    }
    
    @Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
    
    private String hashSenha(String senha) {
        return Integer.toString(senha.hashCode());
    }
    
    private boolean verificarSenha(String senha) {
        return senhaHash.equals(Integer.toString(senha.hashCode()));
    }
}

// ==================== RELATÓRIOS (POLIMORFISMO) ====================
interface Relatorio {
    String gerarConteudo();
    String getTitulo();
    void exibir();
}

class RelatorioAluno implements Relatorio {
    private final AlunoAutenticavel aluno;
    
    public RelatorioAluno(AlunoAutenticavel aluno) {
        this.aluno = aluno;
    }
    
    @Override
    public String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Nome: %s\n", aluno.getNome()));
        sb.append(String.format("Matrícula: %s\n", aluno.getMatricula()));
        sb.append(String.format("Email: %s\n", aluno.getEmail()));
        sb.append(String.format("Tipo: %s\n", aluno.getTipo()));
        
        List<Avaliacao> avaliacoes = aluno.getAvaliacoes();
        if (!avaliacoes.isEmpty()) {
            sb.append("\n📊 AVALIAÇÕES:\n");
            for (Avaliacao av : avaliacoes) {
                sb.append(String.format("  - %s (%s): %.1f (Peso: %.2f)\n", 
                    av.getDescricao(), av.getTipo(), av.getNota(), av.getPeso()));
            }
            sb.append(String.format("\nMédia Final: %.2f\n", aluno.calcularMedia()));
            sb.append(String.format("Status: %s\n", aluno.getStatus()));
        } else {
            sb.append("\nℹ️  Nenhuma avaliação registrada.\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DO ALUNO - %s", aluno.getNome());
    }
    
    @Override
    public void exibir() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(getTitulo());
        System.out.println("=".repeat(50));
        System.out.println(gerarConteudo());
    }
}

class RelatorioProfessor implements Relatorio {
    private final ProfessorAutenticavel professor;
    
    public RelatorioProfessor(ProfessorAutenticavel professor) {
        this.professor = professor;
    }
    
    @Override
    public String gerarConteudo() {
        return String.format(
            "Nome: %s\nEspecialidade: %s\nRegistro: %s\nEmail: %s\nTipo: %s",
            professor.getNome(), professor.getEspecialidade(), professor.getRegistro(),
            professor.getEmail(), professor.getTipo()
        );
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DO PROFESSOR - %s", professor.getNome());
    }
    
    @Override
    public void exibir() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(getTitulo());
        System.out.println("=".repeat(50));
        System.out.println(gerarConteudo());
    }
}

class RelatorioCurso implements Relatorio {
    private final Curso curso;
    
    public RelatorioCurso(Curso curso) {
        this.curso = curso;
    }
    
    @Override
    public String gerarConteudo() {
        String tipo = curso instanceof CursoPresencial ? "Presencial" : 
                     curso instanceof CursoEAD ? "EAD" : "Regular";
        
        String detalhes = "";
        if (curso instanceof CursoPresencial) {
            detalhes = String.format("Sala: %s", ((CursoPresencial)curso).getSala());
        } else if (curso instanceof CursoEAD) {
            detalhes = String.format("Plataforma: %s", ((CursoEAD)curso).getPlataforma());
        }
        
        return String.format(
            "Nome: %s\nCódigo: %s\nCarga Horária: %d horas\nTipo: %s\n%s",
            curso.getNome(), curso.getCodigo(), curso.getCargaHoraria(), tipo, detalhes
        );
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DO CURSO - %s", curso.getNome());
    }
    
    @Override
    public void exibir() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(getTitulo());
        System.out.println("=".repeat(50));
        System.out.println(gerarConteudo());
    }
}

class RelatorioTurma implements Relatorio {
    private final Turma turma;
    
    public RelatorioTurma(Turma turma) {
        this.turma = turma;
    }
    
    @Override
    public String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Código: %s\n", turma.getCodigo()));
        sb.append(String.format("Professor: %s\n", turma.getProfessor().getNome()));
        sb.append(String.format("Curso: %s\n", turma.getCurso().getNome()));
        sb.append(String.format("Quantidade de alunos: %d\n", turma.getQuantidadeAlunos()));
        
        if (turma.getQuantidadeAlunos() > 0) {
            sb.append("\nAlunos matriculados:\n");
            for (Aluno aluno : turma.getAlunos()) {
                sb.append(String.format("- %s (%s)\n", aluno.getNome(), aluno.getMatricula()));
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DA TURMA - %s", turma.getCodigo());
    }
    
    @Override
    public void exibir() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(getTitulo());
        System.out.println("=".repeat(50));
        System.out.println(gerarConteudo());
    }
}

class RelatorioAvaliacoesTurma implements Relatorio {
    private final AvaliacaoTurma avaliacaoTurma;
    
    public RelatorioAvaliacoesTurma(AvaliacaoTurma avaliacaoTurma) {
        this.avaliacaoTurma = avaliacaoTurma;
    }
    
    @Override
    public String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        Turma turma = avaliacaoTurma.getTurma();
        
        sb.append(String.format("Turma: %s\n", turma.getCodigo()));
        sb.append(String.format("Curso: %s\n", turma.getCurso().getNome()));
        sb.append(String.format("Professor: %s\n", turma.getProfessor().getNome()));
        sb.append(String.format("Média da turma: %.2f\n", avaliacaoTurma.calcularMediaTurma()));
        
        Aluno melhorAluno = avaliacaoTurma.getMelhorAluno();
        if (melhorAluno != null) {
            sb.append(String.format("Melhor aluno: %s\n", melhorAluno.getNome()));
        }
        
        sb.append("\n📊 DESEMPENHO DOS ALUNOS:\n");
        for (AvaliacaoAluno aa : avaliacaoTurma.getAvaliacoesAlunos()) {
            sb.append(String.format("  %s - Média: %.2f - Status: %s\n", 
                aa.getAluno().getNome(), aa.calcularMedia(), aa.getStatus()));
        }
        
        return sb.toString();
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DE AVALIAÇÕES - %s", 
            avaliacaoTurma.getTurma().getCodigo());
    }
    
    @Override
    public void exibir() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(getTitulo());
        System.out.println("=".repeat(60));
        System.out.println(gerarConteudo());
    }
}

class RelatorioGeral implements Relatorio {
    private final SistemaEducacionalService service;
    
    public RelatorioGeral(SistemaEducacionalService service) {
        this.service = service;
    }
    
    @Override
    public String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        
        // Estatísticas gerais
        sb.append("📈 ESTATÍSTICAS GERAIS DO SISTEMA\n");
        sb.append("=".repeat(40)).append("\n");
        
        int totalAlunos = service.getAlunoRepository().listarTodos().size();
        int totalProfessores = service.getUsuarioRepository().listarTodos().stream()
            .filter(u -> u instanceof ProfessorAutenticavel).toList().size();
        int totalCursos = service.getCursoRepository().listarTodos().size();
        int totalTurmas = service.getTurmaRepository().listarTodos().size();
        int totalAvaliacoes = service.getAvaliacaoRepository().getTotalAvaliacoes();
        
        sb.append(String.format("Total de Alunos: %d\n", totalAlunos));
        sb.append(String.format("Total de Professores: %d\n", totalProfessores));
        sb.append(String.format("Total de Cursos: %d\n", totalCursos));
        sb.append(String.format("Total de Turmas: %d\n", totalTurmas));
        sb.append(String.format("Total de Avaliações Registradas: %d\n", totalAvaliacoes));
        
        // Distribuição de cursos
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        long presenciais = cursos.stream().filter(c -> c instanceof CursoPresencial).count();
        long ead = cursos.stream().filter(c -> c instanceof CursoEAD).count();
        long regulares = cursos.size() - presenciais - ead;
        
        sb.append("\n📚 DISTRIBUIÇÃO DE CURSOS:\n");
        sb.append(String.format("  Cursos Presenciais: %d\n", presenciais));
        sb.append(String.format("  Cursos EAD: %d\n", ead));
        sb.append(String.format("  Cursos Regulares: %d\n", regulares));
        
        // Turmas com mais alunos
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (!turmas.isEmpty()) {
            sb.append("\n🏫 TURMAS (maior para menor):\n");
            turmas.stream()
                .sorted((t1, t2) -> Integer.compare(t2.getQuantidadeAlunos(), t1.getQuantidadeAlunos()))
                .forEach(t -> sb.append(String.format("  %s: %d alunos\n", 
                    t.getCodigo(), t.getQuantidadeAlunos())));
        }
        
        // Avaliações por turma
        List<AvaliacaoTurma> avaliacoesTurmas = service.getAvaliacaoRepository()
            .listarAvaliacoesTurma();
        if (!avaliacoesTurmas.isEmpty()) {
            sb.append("\n🎯 DESEMPENHO POR TURMA:\n");
            for (AvaliacaoTurma at : avaliacoesTurmas) {
                sb.append(String.format("  %s: Média %.2f\n", 
                    at.getTurma().getCodigo(), at.calcularMediaTurma()));
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String getTitulo() {
        return "RELATÓRIO GERAL DO SISTEMA EDUCACIONAL";
    }
    
    @Override
    public void exibir() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(getTitulo());
        System.out.println("=".repeat(70));
        System.out.println(gerarConteudo());
    }
}

// ==================== REPOSITORY (DATA ACCESS) ====================
interface Repositorio<T> {
    void salvar(T entidade);
    void remover(T entidade);
    List<T> listarTodos();
    T buscarPorId(String id);
    boolean existe(String id);
}

class AlunoRepository implements Repositorio<Aluno> {
    private final List<Aluno> alunos = new ArrayList<>();
    private final Logger logger;
    
    public AlunoRepository(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void salvar(Aluno aluno) {
        if (!alunos.contains(aluno)) {
            alunos.add(aluno);
            logger.debug(String.format("Aluno %s salvo no repositório", aluno.getNome()));
        }
    }
    
    @Override
    public void remover(Aluno aluno) {
        alunos.remove(aluno);
        logger.debug(String.format("Aluno %s removido do repositório", aluno.getNome()));
    }
    
    @Override
    public List<Aluno> listarTodos() {
        return new ArrayList<>(alunos);
    }
    
    @Override
    public Aluno buscarPorId(String matricula) {
        return alunos.stream()
            .filter(a -> a.getMatricula().equals(matricula))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public boolean existe(String matricula) {
        return alunos.stream().anyMatch(a -> a.getMatricula().equals(matricula));
    }
    
    public boolean existeAlunoNoCurso(String curso) {
        return alunos.stream().anyMatch(a -> a.getCurso().equalsIgnoreCase(curso));
    }
    
    public List<Aluno> buscarPorCurso(String curso) {
        return alunos.stream()
            .filter(a -> a.getCurso().equalsIgnoreCase(curso))
            .collect(Collectors.toList());
    }
}

class UsuarioRepository implements Repositorio<Usuario> {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final Logger logger;
    
    public UsuarioRepository(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void salvar(Usuario usuario) {
        if (!usuarios.contains(usuario)) {
            usuarios.add(usuario);
            logger.debug(String.format("Usuário %s salvo no repositório", usuario.getNome()));
        }
    }
    
    @Override
    public void remover(Usuario usuario) {
        usuarios.remove(usuario);
        logger.debug(String.format("Usuário %s removido do repositório", usuario.getNome()));
    }
    
    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }
    
    @Override
    public Usuario buscarPorId(String email) {
        return usuarios.stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public boolean existe(String email) {
        return usuarios.stream().anyMatch(u -> u.getEmail().equals(email));
    }
    
    public Usuario autenticar(Credenciais credenciais) {
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Autenticavel) {
                ResultadoAutenticacao resultado = ((Autenticavel) usuario).autenticar(credenciais);
                if (resultado.isSucesso()) {
                    logger.info(resultado.getMensagem());
                    return usuario;
                }
            }
        }
        logger.erro("Falha na autenticação para login: " + credenciais.getLogin());
        return null;
    }
    
    public List<AlunoAutenticavel> listarAlunosAutenticaveis() {
        return usuarios.stream()
            .filter(u -> u instanceof AlunoAutenticavel)
            .map(u -> (AlunoAutenticavel) u)
            .collect(Collectors.toList());
    }
}

class CursoRepository implements Repositorio<Curso> {
    private final List<Curso> cursos = new ArrayList<>();
    private final Logger logger;
    
    public CursoRepository(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void salvar(Curso curso) {
        if (!cursos.contains(curso)) {
            cursos.add(curso);
            logger.debug(String.format("Curso %s salvo no repositório", curso.getNome()));
        }
    }
    
    @Override
    public void remover(Curso curso) {
        cursos.remove(curso);
        logger.debug(String.format("Curso %s removido do repositório", curso.getNome()));
    }
    
    @Override
    public List<Curso> listarTodos() {
        return new ArrayList<>(cursos);
    }
    
    @Override
    public Curso buscarPorId(String codigo) {
        return cursos.stream()
            .filter(c -> c.getCodigo().equalsIgnoreCase(codigo))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public boolean existe(String codigo) {
        return cursos.stream().anyMatch(c -> c.getCodigo().equalsIgnoreCase(codigo));
    }
    
    public boolean existePorNome(String nome) {
        return cursos.stream().anyMatch(c -> c.getNome().equalsIgnoreCase(nome));
    }
    
    public Curso buscarPorNome(String nome) {
        return cursos.stream()
            .filter(c -> c.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElse(null);
    }
}

class TurmaRepository implements Repositorio<Turma> {
    private final List<Turma> turmas = new ArrayList<>();
    private final Logger logger;
    
    public TurmaRepository(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void salvar(Turma turma) {
        if (!turmas.contains(turma)) {
            turmas.add(turma);
            logger.debug(String.format("Turma %s salva no repositório", turma.getCodigo()));
        }
    }
    
    @Override
    public void remover(Turma turma) {
        turmas.remove(turma);
        logger.debug(String.format("Turma %s removida do repositório", turma.getCodigo()));
    }
    
    @Override
    public List<Turma> listarTodos() {
        return new ArrayList<>(turmas);
    }
    
    @Override
    public Turma buscarPorId(String codigo) {
        return turmas.stream()
            .filter(t -> t.getCodigo().equals(codigo))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public boolean existe(String codigo) {
        return turmas.stream().anyMatch(t -> t.getCodigo().equals(codigo));
    }
    
    public boolean existeTurmaComCurso(String codigoCurso) {
        return turmas.stream().anyMatch(t -> t.getCurso().getCodigo().equalsIgnoreCase(codigoCurso));
    }
    
    public List<Turma> buscarPorCurso(String codigoCurso) {
        return turmas.stream()
            .filter(t -> t.getCurso().getCodigo().equalsIgnoreCase(codigoCurso))
            .collect(Collectors.toList());
    }
    
    public List<Turma> buscarPorProfessor(String registroProfessor) {
        return turmas.stream()
            .filter(t -> t.getProfessor().getRegistro().equals(registroProfessor))
            .collect(Collectors.toList());
    }
}

class AvaliacaoRepository {
    private final List<AvaliacaoTurma> avaliacoesTurma = new ArrayList<>();
    private final Logger logger;
    
    public AvaliacaoRepository(Logger logger) {
        this.logger = logger;
    }
    
    public void salvarAvaliacaoTurma(AvaliacaoTurma avaliacaoTurma) {
        // Remover se já existe (atualização)
        avaliacoesTurma.removeIf(at -> at.getTurma().equals(avaliacaoTurma.getTurma()));
        avaliacoesTurma.add(avaliacaoTurma);
        logger.debug(String.format("Avaliações da turma %s salvas", 
            avaliacaoTurma.getTurma().getCodigo()));
    }
    
    public AvaliacaoTurma buscarAvaliacaoTurma(String codigoTurma) {
        return avaliacoesTurma.stream()
            .filter(at -> at.getTurma().getCodigo().equals(codigoTurma))
            .findFirst()
            .orElse(null);
    }
    
    public List<AvaliacaoTurma> listarAvaliacoesTurma() {
        return new ArrayList<>(avaliacoesTurma);
    }
    
    public int getTotalAvaliacoes() {
        return avaliacoesTurma.stream()
            .mapToInt(at -> at.getAvaliacoesAlunos().size())
            .sum();
    }
    
    public boolean existeAvaliacaoTurma(String codigoTurma) {
        return avaliacoesTurma.stream()
            .anyMatch(at -> at.getTurma().getCodigo().equals(codigoTurma));
    }
}

// ==================== VALIDATOR (VALIDAÇÕES CENTRALIZADAS) ====================
class ValidadorSistema {
    private final CursoRepository cursoRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final Logger logger;
    
    public ValidadorSistema(CursoRepository cursoRepository, 
                          AlunoRepository alunoRepository,
                          TurmaRepository turmaRepository,
                          Logger logger) {
        this.cursoRepository = cursoRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.logger = logger;
    }
    
    public ResultadoValidacao validarCursoExiste(String codigoCurso) {
        if (codigoCurso == null || codigoCurso.trim().isEmpty()) {
            return ResultadoValidacao.erro("Código do curso não pode ser vazio");
        }
        
        boolean existe = cursoRepository.existe(codigoCurso);
        if (!existe) {
            return ResultadoValidacao.erro(String.format("Curso com código '%s' não existe no sistema", codigoCurso));
        }
        
        return ResultadoValidacao.sucesso(String.format("Curso '%s' validado com sucesso", codigoCurso));
    }
    
    public ResultadoValidacao validarCursoPorNomeExiste(String nomeCurso) {
        if (nomeCurso == null || nomeCurso.trim().isEmpty()) {
            return ResultadoValidacao.erro("Nome do curso não pode ser vazio");
        }
        
        boolean existe = cursoRepository.existePorNome(nomeCurso);
        if (!existe) {
            return ResultadoValidacao.erro(String.format("Curso '%s' não existe no sistema", nomeCurso));
        }
        
        return ResultadoValidacao.sucesso(String.format("Curso '%s' validado com sucesso", nomeCurso));
    }
    
    public ResultadoValidacao validarAlunoPodeSerMatriculado(String matriculaAluno, String codigoCurso) {
        // Verificar se aluno existe
        if (!alunoRepository.existe(matriculaAluno)) {
            return ResultadoValidacao.erro(String.format("Aluno com matrícula '%s' não existe", matriculaAluno));
        }
        
        // Verificar se curso existe
        ResultadoValidacao validacaoCurso = validarCursoExiste(codigoCurso);
        if (!validacaoCurso.isValido()) {
            return validacaoCurso;
        }
        
        return ResultadoValidacao.sucesso("Aluno pode ser matriculado no curso");
    }
    
    public ResultadoValidacao validarDadosCadastroAluno(String nome, String matricula, String curso) {
        List<String> erros = new ArrayList<>();
        
        if (nome == null || nome.trim().isEmpty()) {
            erros.add("Nome não pode ser vazio");
        }
        
        if (matricula == null || matricula.trim().isEmpty()) {
            erros.add("Matrícula não pode ser vazia");
        }
        
        if (curso == null || curso.trim().isEmpty()) {
            erros.add("Curso não pode ser vazio");
        } else {
            // Validar se o curso existe no sistema
            ResultadoValidacao validacaoCurso = validarCursoPorNomeExiste(curso);
            if (!validacaoCurso.isValido()) {
                erros.add(validacaoCurso.getMensagem());
            }
        }
        
        if (alunoRepository.existe(matricula)) {
            erros.add(String.format("Matrícula '%s' já está cadastrada", matricula));
        }
        
        if (erros.isEmpty()) {
            return ResultadoValidacao.sucesso("Dados do aluno válidos");
        } else {
            return ResultadoValidacao.erro("Erros de validação: " + String.join(", ", erros));
        }
    }
    
    public ResultadoValidacao validarCriacaoTurma(String codigoTurma, String codigoCurso) {
        List<String> erros = new ArrayList<>();
        
        if (codigoTurma == null || codigoTurma.trim().isEmpty()) {
            erros.add("Código da turma não pode ser vazio");
        }
        
        if (turmaRepository.existe(codigoTurma)) {
            erros.add(String.format("Turma com código '%s' já existe", codigoTurma));
        }
        
        // Validar se curso existe
        ResultadoValidacao validacaoCurso = validarCursoExiste(codigoCurso);
        if (!validacaoCurso.isValido()) {
            erros.add(validacaoCurso.getMensagem());
        }
        
        if (erros.isEmpty()) {
            return ResultadoValidacao.sucesso("Dados da turma válidos");
        } else {
            return ResultadoValidacao.erro("Erros de validação: " + String.join(", ", erros));
        }
    }
    
    public ResultadoValidacao validarAvaliacao(double nota, double peso, String tipo) {
        List<String> erros = new ArrayList<>();
        
        if (nota < 0 || nota > 10) {
            erros.add("Nota deve estar entre 0 e 10");
        }
        
        if (peso <= 0 || peso > 1) {
            erros.add("Peso deve estar entre 0.01 e 1.0");
        }
        
        if (tipo == null || tipo.trim().isEmpty()) {
            erros.add("Tipo da avaliação não pode ser vazio");
        }
        
        if (erros.isEmpty()) {
            return ResultadoValidacao.sucesso("Dados da avaliação válidos");
        } else {
            return ResultadoValidacao.erro("Erros na avaliação: " + String.join(", ", erros));
        }
    }
}

class ResultadoValidacao {
    private final boolean valido;
    private final String mensagem;
    private final List<String> detalhes;
    
    private ResultadoValidacao(boolean valido, String mensagem, List<String> detalhes) {
        this.valido = valido;
        this.mensagem = mensagem;
        this.detalhes = detalhes;
    }
    
    public static ResultadoValidacao sucesso(String mensagem) {
        return new ResultadoValidacao(true, mensagem, new ArrayList<>());
    }
    
    public static ResultadoValidacao erro(String mensagem) {
        List<String> detalhes = new ArrayList<>();
        detalhes.add(mensagem);
        return new ResultadoValidacao(false, "Falha na validação", detalhes);
    }
    
    public static ResultadoValidacao erro(String mensagem, List<String> detalhes) {
        return new ResultadoValidacao(false, mensagem, detalhes);
    }
    
    public boolean isValido() { return valido; }
    public String getMensagem() { return mensagem; }
    public List<String> getDetalhes() { return new ArrayList<>(detalhes); }
}

// ==================== SERVICE (BUSINESS LOGIC) ====================
class SistemaEducacionalService {
    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ValidadorSistema validador;
    private final Logger logger;
    
    public SistemaEducacionalService(Logger logger) {
        this.logger = logger;
        this.alunoRepository = new AlunoRepository(logger);
        this.usuarioRepository = new UsuarioRepository(logger);
        this.cursoRepository = new CursoRepository(logger);
        this.turmaRepository = new TurmaRepository(logger);
        this.avaliacaoRepository = new AvaliacaoRepository(logger);
        this.validador = new ValidadorSistema(cursoRepository, alunoRepository, turmaRepository, logger);
    }
    
    public void carregarDadosIniciais() {
        // Primeiro criar os cursos
        Curso cursoJava = new Curso("Java OO", "JAVA101", 60);
        cursoRepository.salvar(cursoJava);
        
        CursoPresencial cursoJavaAvancado = new CursoPresencial("Java Avançado", "JAVA201", 80, "Sala 301");
        cursoRepository.salvar(cursoJavaAvancado);
        
        CursoEAD cursoPython = new CursoEAD("Python", "PY101", 40, "Plataforma Virtual");
        cursoRepository.salvar(cursoPython);
        
        // Depois criar alunos (agora os cursos existem)
        Aluno alunoJean = new Aluno("Jean Ricardo Land Miranda", "24231215-5", "Java OO");
        alunoRepository.salvar(alunoJean);
        
        Aluno alunoMaria = new Aluno("Maria Silva", "002", "Java OO");
        alunoRepository.salvar(alunoMaria);
        
        Aluno alunoPedro = new Aluno("Pedro Santos", "003", "Python");
        alunoRepository.salvar(alunoPedro);
        
        AlunoAutenticavel alunoAuth = new AlunoAutenticavel("Jean", "jean@edu.com", 
                                                          "24231215-5", "jean", "123");
        usuarioRepository.salvar(alunoAuth);
        
        Professor professorCarlos = new Professor("Carlos", "POO", "PROF001");
        ProfessorAutenticavel profAuth = new ProfessorAutenticavel("Carlos", "carlos@edu.com",
                                                                  "POO", "PROF001", "carlos", "456");
        usuarioRepository.salvar(profAuth);
        
        Administrador admin = new Administrador("Admin", "admin@edu.com", "admin", "admin123");
        usuarioRepository.salvar(admin);
        
        // Criar turma (agora curso e professor existem)
        Turma turma = new Turma("TURMA2024", professorCarlos, cursoJava);
        turma.adicionarAluno(alunoJean);
        turma.adicionarAluno(alunoMaria);
        turmaRepository.salvar(turma);
        
        // Criar avaliações de exemplo
        AvaliacaoTurma avaliacaoTurma = new AvaliacaoTurma(turma);
        
        // Avaliações para aluno Jean
        AvaliacaoAluno avaliacaoJean = new AvaliacaoAluno(alunoJean);
        avaliacaoJean.adicionarAvaliacao(new Avaliacao("Prova 1", "Prova", 0.3));
        avaliacaoJean.adicionarAvaliacao(new Avaliacao("Trabalho", "Trabalho", 0.2));
        avaliacaoJean.adicionarAvaliacao(new Avaliacao("Prova Final", "Prova", 0.5));
        
        // Atribuir notas
        avaliacaoJean.getAvaliacoes().get(0).atribuirNota(8.5);
        avaliacaoJean.getAvaliacoes().get(1).atribuirNota(9.0);
        avaliacaoJean.getAvaliacoes().get(2).atribuirNota(7.5);
        
        // Avaliações para aluno Maria
        AvaliacaoAluno avaliacaoMaria = new AvaliacaoAluno(alunoMaria);
        avaliacaoMaria.adicionarAvaliacao(new Avaliacao("Prova 1", "Prova", 0.3));
        avaliacaoMaria.adicionarAvaliacao(new Avaliacao("Trabalho", "Trabalho", 0.2));
        avaliacaoMaria.adicionarAvaliacao(new Avaliacao("Prova Final", "Prova", 0.5));
        
        avaliacaoMaria.getAvaliacoes().get(0).atribuirNota(9.0);
        avaliacaoMaria.getAvaliacoes().get(1).atribuirNota(8.5);
        avaliacaoMaria.getAvaliacoes().get(2).atribuirNota(8.0);
        
        avaliacaoTurma.adicionarAvaliacaoAluno(avaliacaoJean);
        avaliacaoTurma.adicionarAvaliacaoAluno(avaliacaoMaria);
        avaliacaoRepository.salvarAvaliacaoTurma(avaliacaoTurma);
        
        logger.sucesso("Dados iniciais carregados com sucesso");
    }
    
    public ResultadoOperacao matricularAlunoEmTurma(String matriculaAluno, String codigoTurma) {
        try {
            Aluno aluno = alunoRepository.buscarPorId(matriculaAluno);
            Turma turma = turmaRepository.buscarPorId(codigoTurma);
            
            if (aluno == null) {
                return ResultadoOperacao.erro("Aluno não encontrado");
            }
            
            if (turma == null) {
                return ResultadoOperacao.erro("Turma não encontrada");
            }
            
            ResultadoValidacao validacao = validador.validarAlunoPodeSerMatriculado(
                matriculaAluno, turma.getCurso().getCodigo());
            
            if (!validacao.isValido()) {
                return ResultadoOperacao.erro("Não foi possível matricular aluno: " + 
                    validacao.getMensagem());
            }
            
            turma.adicionarAluno(aluno);
            turmaRepository.salvar(turma);
            
            String mensagem = String.format("Aluno %s matriculado na turma %s", 
                                          aluno.getNome(), turma.getCodigo());
            logger.info(mensagem);
            return ResultadoOperacao.sucesso(mensagem);
        } catch (Exception e) {
            logger.erro("Erro ao matricular aluno: " + e.getMessage());
            return ResultadoOperacao.erro("Erro ao realizar matrícula: " + e.getMessage());
        }
    }
    
    public ResultadoOperacao cadastrarAluno(String nome, String matricula, String curso) {
        try {
            ResultadoValidacao validacao = validador.validarDadosCadastroAluno(nome, matricula, curso);
            
            if (!validacao.isValido()) {
                return ResultadoOperacao.erro("Erro ao cadastrar aluno: " + 
                    String.join(", ", validacao.getDetalhes()));
            }
            
            Aluno aluno = new Aluno(nome, matricula, curso);
            alunoRepository.salvar(aluno);
            
            String mensagem = String.format("Aluno %s cadastrado com sucesso no curso %s", 
                                          nome, curso);
            logger.sucesso(mensagem);
            return ResultadoOperacao.sucesso(mensagem, aluno);
        } catch (Exception e) {
            logger.erro("Erro ao cadastrar aluno: " + e.getMessage());
            return ResultadoOperacao.erro("Erro ao cadastrar aluno: " + e.getMessage());
        }
    }
    
    public ResultadoOperacao cadastrarCurso(String nome, String codigo, int cargaHoraria, 
                                          String tipo, String informacaoAdicional) {
        try {
            if (cursoRepository.existe(codigo)) {
                return ResultadoOperacao.erro(String.format("Curso com código '%s' já existe", codigo));
            }
            
            if (cursoRepository.existePorNome(nome)) {
                return ResultadoOperacao.erro(String.format("Curso '%s' já existe", nome));
            }
            
            Curso curso;
            switch (tipo) {
                case "PRESENCIAL":
                    curso = new CursoPresencial(nome, codigo, cargaHoraria, informacaoAdicional);
                    break;
                case "EAD":
                    curso = new CursoEAD(nome, codigo, cargaHoraria, informacaoAdicional);
                    break;
                default:
                    curso = new Curso(nome, codigo, cargaHoraria);
            }
            
            cursoRepository.salvar(curso);
            
            String mensagem = String.format("Curso %s cadastrado com sucesso", nome);
            logger.sucesso(mensagem);
            return ResultadoOperacao.sucesso(mensagem, curso);
        } catch (Exception e) {
            logger.erro("Erro ao cadastrar curso: " + e.getMessage());
            return ResultadoOperacao.erro("Erro ao cadastrar curso: " + e.getMessage());
        }
    }
    
    public ResultadoOperacao criarTurma(String codigoTurma, Professor professor, Curso curso) {
        try {
            ResultadoValidacao validacao = validador.validarCriacaoTurma(codigoTurma, curso.getCodigo());
            
            if (!validacao.isValido()) {
                return ResultadoOperacao.erro("Erro ao criar turma: " + 
                    String.join(", ", validacao.getDetalhes()));
            }
            
            Turma turma = new Turma(codigoTurma, professor, curso);
            turmaRepository.salvar(turma);
            
            String mensagem = String.format("Turma %s criada com sucesso para o curso %s", 
                                          codigoTurma, curso.getNome());
            logger.sucesso(mensagem);
            return ResultadoOperacao.sucesso(mensagem, turma);
        } catch (Exception e) {
            logger.erro("Erro ao criar turma: " + e.getMessage());
            return ResultadoOperacao.erro("Erro ao criar turma: " + e.getMessage());
        }
    }
    
    public ResultadoOperacao registrarAvaliacao(String codigoTurma, String matriculaAluno, 
                                               String descricao, String tipo, double peso, double nota) {
        try {
            // Validar dados da avaliação
            ResultadoValidacao validacaoAvaliacao = validador.validarAvaliacao(nota, peso, tipo);
            if (!validacaoAvaliacao.isValido()) {
                return ResultadoOperacao.erro("Erro na avaliação: " + 
                    String.join(", ", validacaoAvaliacao.getDetalhes()));
            }
            
            // Buscar turma e aluno
            Turma turma = turmaRepository.buscarPorId(codigoTurma);
            if (turma == null) {
                return ResultadoOperacao.erro("Turma não encontrada");
            }
            
            Aluno aluno = alunoRepository.buscarPorId(matriculaAluno);
            if (aluno == null) {
                return ResultadoOperacao.erro("Aluno não encontrado");
            }
            
            // Verificar se aluno está na turma
            boolean alunoNaTurma = turma.getAlunos().stream()
                .anyMatch(a -> a.getMatricula().equals(matriculaAluno));
            
            if (!alunoNaTurma) {
                return ResultadoOperacao.erro(String.format(
                    "Aluno %s não está matriculado na turma %s", aluno.getNome(), turma.getCodigo()));
            }
            
            // Buscar ou criar avaliação da turma
            AvaliacaoTurma avaliacaoTurma = avaliacaoRepository.buscarAvaliacaoTurma(codigoTurma);
            if (avaliacaoTurma == null) {
                avaliacaoTurma = new AvaliacaoTurma(turma);
            }
            
            // Buscar ou criar avaliação do aluno
            AvaliacaoAluno avaliacaoAluno = avaliacaoTurma.getAvaliacoesAlunos().stream()
                .filter(aa -> aa.getAluno().getMatricula().equals(matriculaAluno))
                .findFirst()
                .orElse(null);
            
            if (avaliacaoAluno == null) {
                avaliacaoAluno = new AvaliacaoAluno(aluno);
                avaliacaoTurma.adicionarAvaliacaoAluno(avaliacaoAluno);
            }
            
            // Criar e adicionar a nova avaliação
            Avaliacao avaliacao = new Avaliacao(descricao, tipo, peso);
            avaliacao.atribuirNota(nota);
            avaliacaoAluno.adicionarAvaliacao(avaliacao);
            
            // Salvar no repositório
            avaliacaoRepository.salvarAvaliacaoTurma(avaliacaoTurma);
            
            String mensagem = String.format("Avaliação '%s' registrada para aluno %s na turma %s", 
                                          descricao, aluno.getNome(), turma.getCodigo());
            logger.sucesso(mensagem);
            return ResultadoOperacao.sucesso(mensagem);
        } catch (Exception e) {
            logger.erro("Erro ao registrar avaliação: " + e.getMessage());
            return ResultadoOperacao.erro("Erro ao registrar avaliação: " + e.getMessage());
        }
    }
    
    public List<Relatorio> gerarRelatoriosCompletos() {
        List<Relatorio> relatorios = new ArrayList<>();
        
        // Gerar relatórios de usuários
        for (Usuario usuario : usuarioRepository.listarTodos()) {
            if (usuario instanceof AlunoAutenticavel) {
                relatorios.add(new RelatorioAluno((AlunoAutenticavel) usuario));
            } else if (usuario instanceof ProfessorAutenticavel) {
                relatorios.add(new RelatorioProfessor((ProfessorAutenticavel) usuario));
            }
        }
        
        // Gerar relatórios de cursos
        for (Curso curso : cursoRepository.listarTodos()) {
            relatorios.add(new RelatorioCurso(curso));
        }
        
        // Gerar relatórios de turmas
        for (Turma turma : turmaRepository.listarTodos()) {
            relatorios.add(new RelatorioTurma(turma));
        }
        
        // Gerar relatórios de avaliações
        for (AvaliacaoTurma avaliacaoTurma : avaliacaoRepository.listarAvaliacoesTurma()) {
            relatorios.add(new RelatorioAvaliacoesTurma(avaliacaoTurma));
        }
        
        // Gerar relatório geral
        relatorios.add(new RelatorioGeral(this));
        
        logger.info(String.format("Gerados %d relatórios", relatorios.size()));
        return relatorios;
    }
    
    public RelatorioGeral gerarRelatorioGeral() {
        return new RelatorioGeral(this);
    }
    
    // Getters para os repositórios
    public AlunoRepository getAlunoRepository() { return alunoRepository; }
    public UsuarioRepository getUsuarioRepository() { return usuarioRepository; }
    public CursoRepository getCursoRepository() { return cursoRepository; }
    public TurmaRepository getTurmaRepository() { return turmaRepository; }
    public AvaliacaoRepository getAvaliacaoRepository() { return avaliacaoRepository; }
    public ValidadorSistema getValidador() { return validador; }
}

class ResultadoOperacao {
    private final boolean sucesso;
    private final String mensagem;
    private final Object dados;
    
    private ResultadoOperacao(boolean sucesso, String mensagem, Object dados) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dados = dados;
    }
    
    public static ResultadoOperacao sucesso(String mensagem) {
        return new ResultadoOperacao(true, mensagem, null);
    }
    
    public static ResultadoOperacao sucesso(String mensagem, Object dados) {
        return new ResultadoOperacao(true, mensagem, dados);
    }
    
    public static ResultadoOperacao erro(String mensagem) {
        return new ResultadoOperacao(false, mensagem, null);
    }
    
    public boolean isSucesso() { return sucesso; }
    public String getMensagem() { return mensagem; }
    public Object getDados() { return dados; }
}

// ==================== UI (USER INTERFACE) ====================
class ConsoleLogger implements Logger {
    @Override
    public void info(String mensagem) {
        System.out.println("ℹ️  " + mensagem);
    }
    
    @Override
    public void erro(String mensagem) {
        System.out.println("❌ " + mensagem);
    }
    
    @Override
    public void sucesso(String mensagem) {
        System.out.println("✓ " + mensagem);
    }
    
    @Override
    public void debug(String mensagem) {
        System.out.println("🔍 " + mensagem);
    }
    
    @Override
    public void titulo(String mensagem) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(mensagem);
        System.out.println("=".repeat(60));
    }
}

class ConsoleOutputHandler implements OutputHandler {
    private final Logger logger;
    
    public ConsoleOutputHandler(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void mostrarMenu(String menu) {
        logger.titulo("SISTEMA DE GESTÃO EDUCACIONAL - EDUCONNECT");
        System.out.println(menu);
        System.out.print("Escolha uma opção: ");
    }
    
    @Override
    public void mostrarMensagem(String mensagem) {
        System.out.println("📝 " + mensagem);
    }
    
    @Override
    public void mostrarErro(String erro) {
        logger.erro(erro);
    }
    
    @Override
    public void mostrarSucesso(String sucesso) {
        logger.sucesso(sucesso);
    }
    
    @Override
    public void limparTela() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
    
    @Override
    public void mostrarSeparador() {
        System.out.println("\n" + "-".repeat(60));
    }
    
    public void mostrarListaCursos(List<Curso> cursos, String titulo) {
        if (cursos.isEmpty()) {
            mostrarMensagem("Nenhum curso cadastrado.");
            return;
        }
        
        mostrarMensagem(titulo);
        for (int i = 0; i < cursos.size(); i++) {
            Curso c = cursos.get(i);
            if (c instanceof CursoPresencial) {
                mostrarMensagem(String.format("%d. %s (Presencial - Sala: %s)", 
                    i + 1, c.getNome(), ((CursoPresencial)c).getSala()));
            } else if (c instanceof CursoEAD) {
                mostrarMensagem(String.format("%d. %s (EAD - Plataforma: %s)", 
                    i + 1, c.getNome(), ((CursoEAD)c).getPlataforma()));
            } else {
                mostrarMensagem(String.format("%d. %s", i + 1, c.getNome()));
            }
        }
    }
}

class ConsoleInputHandler implements InputHandler {
    private final Scanner scanner;
    
    public ConsoleInputHandler() {
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public String lerString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }
    
    @Override
    public int lerInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }
    
    @Override
    public double lerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }
    
    @Override
    public boolean lerBoolean(String prompt, String mensagemSim, String mensagemNao) {
        while (true) {
            System.out.print(prompt + " (S/N): ");
            String resposta = scanner.nextLine().trim().toUpperCase();
            if (resposta.equals("S")) {
                System.out.println(mensagemSim);
                return true;
            } else if (resposta.equals("N")) {
                System.out.println(mensagemNao);
                return false;
            }
            System.out.println("Por favor, digite S para Sim ou N para Não.");
        }
    }
    
    @Override
    public int lerOpcao(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt + " (" + min + "-" + max + "): ");
                int opcao = Integer.parseInt(scanner.nextLine());
                if (opcao >= min && opcao <= max) {
                    return opcao;
                } else {
                    System.out.println("Por favor, digite uma opção entre " + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
            }
        }
    }
    
    public void fechar() {
        scanner.close();
    }
}

class MenuController {
    private final SistemaEducacionalService service;
    private final OutputHandler output;
    private final InputHandler input;
    private boolean executando;
    
    public MenuController(SistemaEducacionalService service, OutputHandler output, 
                         InputHandler input) {
        this.service = service;
        this.output = output;
        this.input = input;
        this.executando = true;
    }
    
    public void iniciar() {
        mostrarCabecalho();
        service.carregarDadosIniciais();
        
        while (executando) {
            exibirMenuPrincipal();
        }
    }
    
    private void mostrarCabecalho() {
        output.limparTela();
        output.mostrarMensagem("🎓 SISTEMA DE GESTÃO EDUCACIONAL - EDUCONNECT");
        output.mostrarMensagem("=".repeat(60));
        output.mostrarMensagem("Aluno: Jean Ricardo Land Miranda");
        output.mostrarMensagem("RA: 24231215-5");
        output.mostrarMensagem("Curso: Análise e Desenvolvimento de Sistemas");
        output.mostrarMensagem("=".repeat(60));
    }
    
    private void exibirMenuPrincipal() {
        String menu = """
            
            1. Gerenciar Alunos
            2. Gerenciar Professores
            3. Gerenciar Cursos
            4. Gerenciar Turmas
            5. Registrar Avaliações
            6. Gerar Relatórios
            7. Autenticar Usuário
            8. Estatísticas do Sistema
            9. Testar Cenários
            0. Sair
            """;
        
        output.mostrarMenu(menu);
        int opcao = input.lerInt("");
        
        switch (opcao) {
            case 1 -> gerenciarAlunos();
            case 2 -> gerenciarProfessores();
            case 3 -> gerenciarCursos();
            case 4 -> gerenciarTurmas();
            case 5 -> gerenciarAvaliacoes();
            case 6 -> gerenciarRelatorios();
            case 7 -> autenticarUsuario();
            case 8 -> mostrarEstatisticas();
            case 9 -> testarCenarios();
            case 0 -> sair();
            default -> output.mostrarErro("Opção inválida!");
        }
    }
    
    private void gerenciarAlunos() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE ALUNOS");
        output.mostrarSeparador();
        
        List<Aluno> alunos = service.getAlunoRepository().listarTodos();
        if (alunos.isEmpty()) {
            output.mostrarMensagem("Nenhum aluno cadastrado.");
        } else {
            output.mostrarMensagem("Alunos cadastrados:");
            for (int i = 0; i < alunos.size(); i++) {
                Aluno a = alunos.get(i);
                output.mostrarMensagem(String.format("%d. %s - %s - %s", 
                    i + 1, a.getNome(), a.getMatricula(), a.getCurso()));
            }
        }
        
        output.mostrarSeparador();
        boolean adicionar = input.lerBoolean("Deseja adicionar um novo aluno?", 
                                           "Iniciando cadastro...", "Retornando ao menu...");
        if (adicionar) {
            cadastrarAluno();
        }
    }
    
    private void cadastrarAluno() {
        output.mostrarMensagem("\n📝 CADASTRO DE NOVO ALUNO");
        
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarErro("Não é possível cadastrar aluno. Nenhum curso disponível.");
            output.mostrarMensagem("Cadastre um curso primeiro na opção 3 do menu.");
            return;
        }
        
        output.mostrarMensagem("\n📚 CURSOS DISPONÍVEIS:");
        for (int i = 0; i < cursos.size(); i++) {
            output.mostrarMensagem(String.format("%d. %s", i + 1, cursos.get(i).getNome()));
        }
        
        String nome = input.lerString("\nNome completo");
        String matricula = input.lerString("Matrícula");
        
        int cursoIndex;
        do {
            cursoIndex = input.lerInt("Número do curso (1-" + cursos.size() + ")") - 1;
            if (cursoIndex < 0 || cursoIndex >= cursos.size()) {
                output.mostrarErro("Número de curso inválido!");
            }
        } while (cursoIndex < 0 || cursoIndex >= cursos.size());
        
        String cursoSelecionado = cursos.get(cursoIndex).getNome();
        
        ResultadoOperacao resultado = service.cadastrarAluno(nome, matricula, cursoSelecionado);
        
        if (resultado.isSucesso()) {
            output.mostrarSucesso(resultado.getMensagem());
            
            boolean criarUsuario = input.lerBoolean("Deseja criar usuário autenticável para este aluno?",
                                                  "Criando usuário...", "Continuando...");
            if (criarUsuario) {
                String email = input.lerString("Email");
                String login = input.lerString("Login");
                String senha = input.lerString("Senha");
                
                AlunoAutenticavel alunoAuth = new AlunoAutenticavel(nome, email, matricula, login, senha);
                service.getUsuarioRepository().salvar(alunoAuth);
                output.mostrarSucesso("Usuário autenticável criado com sucesso!");
            }
        } else {
            output.mostrarErro(resultado.getMensagem());
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void gerenciarProfessores() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE PROFESSORES");
        output.mostrarSeparador();
        
        List<Usuario> usuarios = service.getUsuarioRepository().listarTodos();
        List<ProfessorAutenticavel> professores = usuarios.stream()
            .filter(u -> u instanceof ProfessorAutenticavel)
            .map(u -> (ProfessorAutenticavel) u)
            .toList();
        
        if (professores.isEmpty()) {
            output.mostrarMensagem("Nenhum professor cadastrado.");
        } else {
            output.mostrarMensagem("Professores cadastrados:");
            for (int i = 0; i < professores.size(); i++) {
                ProfessorAutenticavel p = professores.get(i);
                output.mostrarMensagem(String.format("%d. %s - %s - %s", 
                    i + 1, p.getNome(), p.getEspecialidade(), p.getEmail()));
            }
        }
        
        output.mostrarSeparador();
        boolean adicionar = input.lerBoolean("Deseja adicionar um novo professor?", 
                                           "Iniciando cadastro...", "Retornando ao menu...");
        if (adicionar) {
            cadastrarProfessor();
        }
    }
    
    private void cadastrarProfessor() {
        output.mostrarMensagem("\n📝 CADASTRO DE NOVO PROFESSOR");
        String nome = input.lerString("Nome completo");
        String especialidade = input.lerString("Especialidade");
        String registro = input.lerString("Registro");
        String email = input.lerString("Email");
        String login = input.lerString("Login");
        String senha = input.lerString("Senha");
        
        ProfessorAutenticavel professor = new ProfessorAutenticavel(
            nome, email, especialidade, registro, login, senha);
        
        service.getUsuarioRepository().salvar(professor);
        output.mostrarSucesso("Professor cadastrado com sucesso!");
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void gerenciarCursos() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE CURSOS");
        output.mostrarSeparador();
        
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarMensagem("Nenhum curso cadastrado.");
        } else {
            output.mostrarMensagem("Cursos cadastrados:");
            for (int i = 0; i < cursos.size(); i++) {
                Curso c = cursos.get(i);
                output.mostrarMensagem(String.format("%d. %s", i + 1, c.toString()));
            }
        }
        
        output.mostrarSeparador();
        boolean adicionar = input.lerBoolean("Deseja adicionar um novo curso?", 
                                           "Iniciando cadastro...", "Retornando ao menu...");
        if (adicionar) {
            cadastrarCurso();
        }
    }
    
    private void cadastrarCurso() {
        output.mostrarMensagem("\n📝 CADASTRO DE NOVO CURSO");
        String nome = input.lerString("Nome do curso");
        String codigo = input.lerString("Código");
        int cargaHoraria = input.lerInt("Carga horária (horas)");
        
        output.mostrarMensagem("\nTipo de curso:");
        output.mostrarMensagem("1. Presencial");
        output.mostrarMensagem("2. EAD");
        output.mostrarMensagem("3. Regular");
        
        int tipo = input.lerOpcao("Escolha o tipo", 1, 3);
        
        ResultadoOperacao resultado;
        switch (tipo) {
            case 1 -> {
                String sala = input.lerString("Sala");
                resultado = service.cadastrarCurso(nome, codigo, cargaHoraria, "PRESENCIAL", sala);
            }
            case 2 -> {
                String plataforma = input.lerString("Plataforma");
                resultado = service.cadastrarCurso(nome, codigo, cargaHoraria, "EAD", plataforma);
            }
            default -> {
                resultado = service.cadastrarCurso(nome, codigo, cargaHoraria, "REGULAR", "");
            }
        }
        
        if (resultado.isSucesso()) {
            output.mostrarSucesso(resultado.getMensagem());
        } else {
            output.mostrarErro(resultado.getMensagem());
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void gerenciarTurmas() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE TURMAS");
        output.mostrarSeparador();
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarMensagem("Nenhuma turma cadastrada.");
        } else {
            output.mostrarMensagem("Turmas cadastradas:");
            for (Turma t : turmas) {
                output.mostrarMensagem(String.format("Código: %s | Curso: %s | Professor: %s | Alunos: %d",
                    t.getCodigo(), t.getCurso().getNome(), t.getProfessor().getNome(), 
                    t.getQuantidadeAlunos()));
            }
        }
        
        output.mostrarSeparador();
        output.mostrarMensagem("Opções:");
        output.mostrarMensagem("1. Criar nova turma");
        output.mostrarMensagem("2. Matricular aluno em turma existente");
        output.mostrarMensagem("3. Voltar");
        
        int opcao = input.lerOpcao("Escolha uma opção", 1, 3);
        
        switch (opcao) {
            case 1 -> criarTurma();
            case 2 -> matricularAlunoEmTurmaExistente();
            case 3 -> { return; }
        }
    }
    
    private void criarTurma() {
        output.mostrarMensagem("\n🏫 CRIAÇÃO DE NOVA TURMA");
        
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarErro("Não é possível criar turma. Nenhum curso cadastrado.");
            output.mostrarMensagem("Cadastre um curso primeiro na opção 3 do menu.");
            return;
        }
        
        List<Usuario> usuarios = service.getUsuarioRepository().listarTodos();
        List<ProfessorAutenticavel> professores = usuarios.stream()
            .filter(u -> u instanceof ProfessorAutenticavel)
            .map(u -> (ProfessorAutenticavel) u)
            .toList();
        
        if (professores.isEmpty()) {
            output.mostrarErro("Não é possível criar turma. Nenhum professor cadastrado.");
            output.mostrarMensagem("Cadastre um professor primeiro na opção 2 do menu.");
            return;
        }
        
        String codigoTurma = input.lerString("Código da turma");
        
        output.mostrarMensagem("\nSelecione o curso:");
        for (int i = 0; i < cursos.size(); i++) {
            output.mostrarMensagem(String.format("%d. %s", i + 1, cursos.get(i).getNome()));
        }
        
        int indexCurso = input.lerOpcao("Número do curso", 1, cursos.size()) - 1;
        Curso cursoSelecionado = cursos.get(indexCurso);
        
        output.mostrarMensagem("\nSelecione o professor:");
        for (int i = 0; i < professores.size(); i++) {
            output.mostrarMensagem(String.format("%d. %s", i + 1, professores.get(i).getNome()));
        }
        
        int indexProf = input.lerOpcao("Número do professor", 1, professores.size()) - 1;
        Professor professor = new Professor(
            professores.get(indexProf).getNome(),
            professores.get(indexProf).getEspecialidade(),
            professores.get(indexProf).getRegistro()
        );
        
        ResultadoOperacao resultado = service.criarTurma(codigoTurma, professor, cursoSelecionado);
        
        if (resultado.isSucesso()) {
            Turma turmaCriada = (Turma) resultado.getDados();
            output.mostrarSucesso(resultado.getMensagem());
            
            boolean matricular = input.lerBoolean("Deseja matricular alunos agora?", 
                                                "Iniciando matrículas...", "Pulando matrículas...");
            if (matricular) {
                matricularAlunosNaTurma(turmaCriada);
            }
            
            output.mostrarMensagem("\n📊 RESUMO DA TURMA CRIADA:");
            output.mostrarMensagem("Código: " + turmaCriada.getCodigo());
            output.mostrarMensagem("Professor: " + turmaCriada.getProfessor().getNome());
            output.mostrarMensagem("Curso: " + turmaCriada.getCurso().getNome());
            output.mostrarMensagem("Quantidade de alunos: " + turmaCriada.getQuantidadeAlunos());
        } else {
            output.mostrarErro(resultado.getMensagem());
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void matricularAlunoEmTurmaExistente() {
        output.mostrarMensagem("\n🎓 MATRÍCULA DE ALUNO EM TURMA");
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarErro("Nenhuma turma disponível.");
            return;
        }
        
        output.mostrarMensagem("\nTurmas disponíveis:");
        for (int i = 0; i < turmas.size(); i++) {
            Turma t = turmas.get(i);
            output.mostrarMensagem(String.format("%d. %s - %s (%d alunos)", 
                i + 1, t.getCodigo(), t.getCurso().getNome(), t.getQuantidadeAlunos()));
        }
        
        int indexTurma = input.lerOpcao("Selecione a turma", 1, turmas.size()) - 1;
        Turma turmaSelecionada = turmas.get(indexTurma);
        
        matricularAlunosNaTurma(turmaSelecionada);
    }
    
    private void matricularAlunosNaTurma(Turma turma) {
        List<Aluno> alunos = service.getAlunoRepository().listarTodos();
        if (alunos.isEmpty()) {
            output.mostrarMensagem("Nenhum aluno disponível para matrícula.");
            return;
        }
        
        output.mostrarMensagem("\nAlunos disponíveis:");
        for (int i = 0; i < alunos.size(); i++) {
            Aluno aluno = alunos.get(i);
            boolean jaMatriculado = turma.getAlunos().stream()
                .anyMatch(a -> a.getMatricula().equals(aluno.getMatricula()));
            
            String status = jaMatriculado ? " [JÁ MATRICULADO]" : "";
            output.mostrarMensagem(String.format("%d. %s - %s%s", 
                i + 1, aluno.getNome(), aluno.getCurso(), status));
        }
        
        String continuar = "S";
        while (continuar.equalsIgnoreCase("S")) {
            int indexAluno = input.lerOpcao("\nNúmero do aluno para matricular (0 para cancelar)", 
                                          0, alunos.size()) - 1;
            
            if (indexAluno == -1) {
                break;
            }
            
            Aluno alunoSelecionado = alunos.get(indexAluno);
            
            boolean jaMatriculado = turma.getAlunos().stream()
                .anyMatch(a -> a.getMatricula().equals(alunoSelecionado.getMatricula()));
            
            if (jaMatriculado) {
                output.mostrarErro("Este aluno já está matriculado nesta turma!");
            } else {
                ResultadoValidacao validacao = service.getValidador()
                    .validarAlunoPodeSerMatriculado(alunoSelecionado.getMatricula(), 
                                                   turma.getCurso().getCodigo());
                
                if (validacao.isValido()) {
                    turma.adicionarAluno(alunoSelecionado);
                    service.getTurmaRepository().salvar(turma);
                    output.mostrarSucesso("Aluno " + alunoSelecionado.getNome() + " matriculado com sucesso!");
                } else {
                    output.mostrarErro("Não foi possível matricular aluno: " + validacao.getMensagem());
                }
            }
            
            continuar = input.lerString("Matricular outro aluno? (S/N)").toUpperCase();
        }
    }
    
    private void gerenciarAvaliacoes() {
        output.limparTela();
        output.mostrarMensagem("📊 GERENCIAMENTO DE AVALIAÇÕES");
        output.mostrarSeparador();
        
        output.mostrarMensagem("Opções:");
        output.mostrarMensagem("1. Registrar nova avaliação");
        output.mostrarMensagem("2. Ver avaliações de uma turma");
        output.mostrarMensagem("3. Ver avaliações de um aluno");
        output.mostrarMensagem("4. Voltar");
        
        int opcao = input.lerOpcao("Escolha uma opção", 1, 4);
        
        switch (opcao) {
            case 1 -> registrarAvaliacao();
            case 2 -> verAvaliacoesTurma();
            case 3 -> verAvaliacoesAluno();
            case 4 -> { return; }
        }
    }
    
    private void registrarAvaliacao() {
        output.mostrarMensagem("\n📝 REGISTRO DE NOVA AVALIAÇÃO");
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarErro("Nenhuma turma disponível para registrar avaliações.");
            return;
        }
        
        output.mostrarMensagem("\nTurmas disponíveis:");
        for (int i = 0; i < turmas.size(); i++) {
            Turma t = turmas.get(i);
            output.mostrarMensagem(String.format("%d. %s - %s", 
                i + 1, t.getCodigo(), t.getCurso().getNome()));
        }
        
        int indexTurma = input.lerOpcao("Selecione a turma", 1, turmas.size()) - 1;
        Turma turmaSelecionada = turmas.get(indexTurma);
        
        if (turmaSelecionada.getQuantidadeAlunos() == 0) {
            output.mostrarErro("Esta turma não tem alunos matriculados.");
            return;
        }
        
        output.mostrarMensagem("\nAlunos da turma:");
        List<Aluno> alunosTurma = turmaSelecionada.getAlunos();
        for (int i = 0; i < alunosTurma.size(); i++) {
            Aluno a = alunosTurma.get(i);
            output.mostrarMensagem(String.format("%d. %s", i + 1, a.getNome()));
        }
        
        int indexAluno = input.lerOpcao("Selecione o aluno", 1, alunosTurma.size()) - 1;
        Aluno alunoSelecionado = alunosTurma.get(indexAluno);
        
        String descricao = input.lerString("Descrição da avaliação");
        String tipo = input.lerString("Tipo (Prova, Trabalho, Seminário, etc.)");
        double peso = input.lerDouble("Peso (0.01 a 1.0)");
        double nota = input.lerDouble("Nota (0 a 10)");
        
        ResultadoOperacao resultado = service.registrarAvaliacao(
            turmaSelecionada.getCodigo(),
            alunoSelecionado.getMatricula(),
            descricao,
            tipo,
            peso,
            nota
        );
        
        if (resultado.isSucesso()) {
            output.mostrarSucesso(resultado.getMensagem());
        } else {
            output.mostrarErro(resultado.getMensagem());
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void verAvaliacoesTurma() {
        output.mostrarMensagem("\n📋 VISUALIZAR AVALIAÇÕES DA TURMA");
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarErro("Nenhuma turma disponível.");
            return;
        }
        
        output.mostrarMensagem("\nTurmas disponíveis:");
        for (int i = 0; i < turmas.size(); i++) {
            Turma t = turmas.get(i);
            output.mostrarMensagem(String.format("%d. %s - %s", 
                i + 1, t.getCodigo(), t.getCurso().getNome()));
        }
        
        int indexTurma = input.lerOpcao("Selecione a turma", 1, turmas.size()) - 1;
        Turma turmaSelecionada = turmas.get(indexTurma);
        
        AvaliacaoTurma avaliacaoTurma = service.getAvaliacaoRepository()
            .buscarAvaliacaoTurma(turmaSelecionada.getCodigo());
        
        if (avaliacaoTurma == null) {
            output.mostrarMensagem("Nenhuma avaliação registrada para esta turma.");
        } else {
            Relatorio relatorio = new RelatorioAvaliacoesTurma(avaliacaoTurma);
            relatorio.exibir();
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void verAvaliacoesAluno() {
        output.mostrarMensagem("\n📋 VISUALIZAR AVALIAÇÕES DO ALUNO");
        
        List<Aluno> alunos = service.getAlunoRepository().listarTodos();
        if (alunos.isEmpty()) {
            output.mostrarErro("Nenhum aluno disponível.");
            return;
        }
        
        output.mostrarMensagem("\nAlunos disponíveis:");
        for (int i = 0; i < alunos.size(); i++) {
            Aluno a = alunos.get(i);
            output.mostrarMensagem(String.format("%d. %s - %s", 
                i + 1, a.getNome(), a.getMatricula()));
        }
        
        int indexAluno = input.lerOpcao("Selecione o aluno", 1, alunos.size()) - 1;
        Aluno alunoSelecionado = alunos.get(indexAluno);
        
        // Buscar aluno autenticável
        List<AlunoAutenticavel> alunosAuth = service.getUsuarioRepository()
            .listarAlunosAutenticaveis();
        
        AlunoAutenticavel alunoAuth = alunosAuth.stream()
            .filter(a -> a.getMatricula().equals(alunoSelecionado.getMatricula()))
            .findFirst()
            .orElse(null);
        
        if (alunoAuth == null) {
            output.mostrarMensagem("Este aluno não tem usuário autenticável cadastrado.");
            output.mostrarMensagem("Não há avaliações registradas.");
        } else if (alunoAuth.getAvaliacoes().isEmpty()) {
            output.mostrarMensagem("Nenhuma avaliação registrada para este aluno.");
        } else {
            Relatorio relatorio = new RelatorioAluno(alunoAuth);
            relatorio.exibir();
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void gerenciarRelatorios() {
        output.limparTela();
        output.mostrarMensagem("📄 GERENCIAMENTO DE RELATÓRIOS");
        output.mostrarSeparador();
        
        output.mostrarMensagem("Opções:");
        output.mostrarMensagem("1. Relatório Geral do Sistema");
        output.mostrarMensagem("2. Relatório de Alunos");
        output.mostrarMensagem("3. Relatório de Professores");
        output.mostrarMensagem("4. Relatório de Cursos");
        output.mostrarMensagem("5. Relatório de Turmas");
        output.mostrarMensagem("6. Relatório de Avaliações");
        output.mostrarMensagem("7. Todos os Relatórios");
        output.mostrarMensagem("8. Voltar");
        
        int opcao = input.lerOpcao("Escolha uma opção", 1, 8);
        
        switch (opcao) {
            case 1 -> exibirRelatorioGeral();
            case 2 -> exibirRelatoriosAlunos();
            case 3 -> exibirRelatoriosProfessores();
            case 4 -> exibirRelatoriosCursos();
            case 5 -> exibirRelatoriosTurmas();
            case 6 -> exibirRelatoriosAvaliacoes();
            case 7 -> exibirTodosRelatorios();
            case 8 -> { return; }
        }
    }
    
    private void exibirRelatorioGeral() {
        output.limparTela();
        RelatorioGeral relatorio = service.gerarRelatorioGeral();
        relatorio.exibir();
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void exibirRelatoriosAlunos() {
        output.limparTela();
        output.mostrarMensagem("🎓 RELATÓRIOS DE ALUNOS");
        
        List<AlunoAutenticavel> alunos = service.getUsuarioRepository()
            .listarAlunosAutenticaveis();
        
        if (alunos.isEmpty()) {
            output.mostrarMensagem("Nenhum aluno autenticável cadastrado.");
        } else {
            for (AlunoAutenticavel aluno : alunos) {
                Relatorio relatorio = new RelatorioAluno(aluno);
                relatorio.exibir();
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void exibirRelatoriosProfessores() {
        output.limparTela();
        output.mostrarMensagem("👨‍🏫 RELATÓRIOS DE PROFESSORES");
        
        List<Usuario> usuarios = service.getUsuarioRepository().listarTodos();
        List<ProfessorAutenticavel> professores = usuarios.stream()
            .filter(u -> u instanceof ProfessorAutenticavel)
            .map(u -> (ProfessorAutenticavel) u)
            .toList();
        
        if (professores.isEmpty()) {
            output.mostrarMensagem("Nenhum professor cadastrado.");
        } else {
            for (ProfessorAutenticavel professor : professores) {
                Relatorio relatorio = new RelatorioProfessor(professor);
                relatorio.exibir();
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void exibirRelatoriosCursos() {
        output.limparTela();
        output.mostrarMensagem("📚 RELATÓRIOS DE CURSOS");
        
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarMensagem("Nenhum curso cadastrado.");
        } else {
            for (Curso curso : cursos) {
                Relatorio relatorio = new RelatorioCurso(curso);
                relatorio.exibir();
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void exibirRelatoriosTurmas() {
        output.limparTela();
        output.mostrarMensagem("🏫 RELATÓRIOS DE TURMAS");
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarMensagem("Nenhuma turma cadastrada.");
        } else {
            for (Turma turma : turmas) {
                Relatorio relatorio = new RelatorioTurma(turma);
                relatorio.exibir();
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void exibirRelatoriosAvaliacoes() {
        output.limparTela();
        output.mostrarMensagem("📊 RELATÓRIOS DE AVALIAÇÕES");
        
        List<AvaliacaoTurma> avaliacoes = service.getAvaliacaoRepository()
            .listarAvaliacoesTurma();
        
        if (avaliacoes.isEmpty()) {
            output.mostrarMensagem("Nenhuma avaliação registrada.");
        } else {
            for (AvaliacaoTurma avaliacao : avaliacoes) {
                Relatorio relatorio = new RelatorioAvaliacoesTurma(avaliacao);
                relatorio.exibir();
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void exibirTodosRelatorios() {
        output.limparTela();
        output.mostrarMensagem("📋 TODOS OS RELATÓRIOS DO SISTEMA");
        
        List<Relatorio> relatorios = service.gerarRelatoriosCompletos();
        
        if (relatorios.isEmpty()) {
            output.mostrarMensagem("Nenhum dado disponível para gerar relatórios.");
        } else {
            for (Relatorio relatorio : relatorios) {
                relatorio.exibir();
                output.mostrarSeparador();
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void autenticarUsuario() {
        output.limparTela();
        output.mostrarMensagem("🔐 AUTENTICAÇÃO DE USUÁRIO");
        output.mostrarSeparador();
        
        String login = input.lerString("Login");
        String senha = input.lerString("Senha");
        
        Credenciais credenciais = new Credenciais(login, senha);
        Usuario usuario = service.getUsuarioRepository().autenticar(credenciais);
        
        if (usuario != null) {
            output.mostrarSucesso("Autenticação bem-sucedida!");
            output.mostrarMensagem("Bem-vindo, " + usuario.getNome() + "!");
            output.mostrarMensagem("Tipo: " + usuario.getTipo());
            output.mostrarMensagem("Email: " + usuario.getEmail());
        } else {
            output.mostrarErro("Falha na autenticação. Verifique suas credenciais.");
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void mostrarEstatisticas() {
        output.limparTela();
        output.mostrarMensagem("📈 ESTATÍSTICAS DO SISTEMA");
        output.mostrarSeparador();
        
        int totalAlunos = service.getAlunoRepository().listarTodos().size();
        int totalUsuarios = service.getUsuarioRepository().listarTodos().size();
        int totalCursos = service.getCursoRepository().listarTodos().size();
        int totalTurmas = service.getTurmaRepository().listarTodos().size();
        int totalAvaliacoes = service.getAvaliacaoRepository().getTotalAvaliacoes();
        
        output.mostrarMensagem(String.format("Total de alunos: %d", totalAlunos));
        output.mostrarMensagem(String.format("Total de usuários: %d", totalUsuarios));
        output.mostrarMensagem(String.format("Total de cursos: %d", totalCursos));
        output.mostrarMensagem(String.format("Total de turmas: %d", totalTurmas));
        output.mostrarMensagem(String.format("Total de avaliações: %d", totalAvaliacoes));
        
        List<Usuario> usuarios = service.getUsuarioRepository().listarTodos();
        long alunosAuth = usuarios.stream().filter(u -> u instanceof AlunoAutenticavel).count();
        long professores = usuarios.stream().filter(u -> u instanceof ProfessorAutenticavel).count();
        long admins = usuarios.stream().filter(u -> u instanceof Administrador).count();
        
        output.mostrarSeparador();
        output.mostrarMensagem("\nDistribuição de usuários:");
        output.mostrarMensagem(String.format("Alunos autenticáveis: %d", alunosAuth));
        output.mostrarMensagem(String.format("Professores: %d", professores));
        output.mostrarMensagem(String.format("Administradores: %d", admins));
        
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        long cursosPresenciais = cursos.stream().filter(c -> c instanceof CursoPresencial).count();
        long cursosEAD = cursos.stream().filter(c -> c instanceof CursoEAD).count();
        long cursosRegulares = cursos.size() - cursosPresenciais - cursosEAD;
        
        output.mostrarSeparador();
        output.mostrarMensagem("\nDistribuição de cursos:");
        output.mostrarMensagem(String.format("Cursos presenciais: %d", cursosPresenciais));
        output.mostrarMensagem(String.format("Cursos EAD: %d", cursosEAD));
        output.mostrarMensagem(String.format("Cursos regulares: %d", cursosRegulares));
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void testarCenarios() {
        output.limparTela();
        output.mostrarMensagem("🧪 TESTAR CENÁRIOS DE SUCESSO E FALHA");
        output.mostrarSeparador();
        
        output.mostrarMensagem("Cenários disponíveis:");
        output.mostrarMensagem("1. Testar cadastro de aluno com curso existente (SUCESSO)");
        output.mostrarMensagem("2. Testar cadastro de aluno com curso inexistente (FALHA)");
        output.mostrarMensagem("3. Testar criação de turma sem curso (FALHA)");
        output.mostrarMensagem("4. Testar criação de turma sem professor (FALHA)");
        output.mostrarMensagem("5. Testar registro de avaliação com nota inválida (FALHA)");
        output.mostrarMensagem("6. Testar matrícula de aluno já matriculado (FALHA)");
        output.mostrarMensagem("7. Voltar");
        
        int opcao = input.lerOpcao("Escolha um cenário", 1, 7);
        
        switch (opcao) {
            case 1 -> testarCadastroAlunoSucesso();
            case 2 -> testarCadastroAlunoFalha();
            case 3 -> testarCriacaoTurmaSemCurso();
            case 4 -> testarCriacaoTurmaSemProfessor();
            case 5 -> testarAvaliacaoNotaInvalida();
            case 6 -> testarMatriculaDuplicada();
            case 7 -> { return; }
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void testarCadastroAlunoSucesso() {
        output.mostrarMensagem("\n✅ CENÁRIO 1: Cadastro de aluno com curso existente");
        
        // Verificar se há cursos disponíveis
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarErro("Não há cursos cadastrados para testar.");
            return;
        }
        
        String nome = "Aluno Teste Sucesso";
        String matricula = "TESTE001";
        String curso = cursos.get(0).getNome(); // Usar primeiro curso disponível
        
        output.mostrarMensagem(String.format("Tentando cadastrar aluno '%s' no curso '%s'", nome, curso));
        
        ResultadoOperacao resultado = service.cadastrarAluno(nome, matricula, curso);
        
        if (resultado.isSucesso()) {
            output.mostrarSucesso("✅ TESTE PASSOU: " + resultado.getMensagem());
        } else {
            output.mostrarErro("❌ TESTE FALHOU: " + resultado.getMensagem());
        }
    }
    
    private void testarCadastroAlunoFalha() {
        output.mostrarMensagem("\n❌ CENÁRIO 2: Cadastro de aluno com curso inexistente");
        
        String nome = "Aluno Teste Falha";
        String matricula = "TESTE002";
        String curso = "Curso Que Não Existe";
        
        output.mostrarMensagem(String.format("Tentando cadastrar aluno '%s' no curso '%s' (inexistente)", nome, curso));
        
        ResultadoOperacao resultado = service.cadastrarAluno(nome, matricula, curso);
        
        if (!resultado.isSucesso()) {
            output.mostrarSucesso("✅ TESTE PASSOU (esperava falha): " + resultado.getMensagem());
        } else {
            output.mostrarErro("❌ TESTE FALHOU: Cadastro não deveria ter sucesso!");
        }
    }
    
    private void testarCriacaoTurmaSemCurso() {
        output.mostrarMensagem("\n❌ CENÁRIO 3: Criação de turma sem cursos cadastrados");
        
        // Temporariamente remover cursos para testar
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        boolean haviaCursos = !cursos.isEmpty();
        
        output.mostrarMensagem("Situação: " + (haviaCursos ? "Há cursos cadastrados" : "Não há cursos cadastrados"));
        
        if (haviaCursos) {
            output.mostrarMensagem("Este teste requer que não haja cursos cadastrados.");
            output.mostrarMensagem("Execute após remover todos os cursos.");
        } else {
            Professor professor = new Professor("Professor Teste", "Teste", "TESTE001");
            Curso curso = new Curso("Curso Inexistente", "INVALIDO", 0);
            
            ResultadoOperacao resultado = service.criarTurma("TURMATESTE", professor, curso);
            
            if (!resultado.isSucesso()) {
                output.mostrarSucesso("✅ TESTE PASSOU (esperava falha): " + resultado.getMensagem());
            } else {
                output.mostrarErro("❌ TESTE FALHOU: Criação não deveria ter sucesso!");
            }
        }
    }
    
    private void testarCriacaoTurmaSemProfessor() {
        output.mostrarMensagem("\n❌ CENÁRIO 4: Criação de turma sem professores cadastrados");
        
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarErro("Não há cursos para testar.");
            return;
        }
        
        Curso curso = cursos.get(0);
        Professor professor = new Professor("Professor Inexistente", "Teste", "INVALIDO");
        
        output.mostrarMensagem(String.format("Tentando criar turma com professor não cadastrado no curso '%s'", curso.getNome()));
        
        ResultadoOperacao resultado = service.criarTurma("TURMATESTE2", professor, curso);
        
        // Este teste pode passar ou falhar dependendo da implementação
        // O importante é que não cause erro no sistema
        output.mostrarMensagem("Resultado: " + (resultado.isSucesso() ? "Sucesso" : "Falha"));
        output.mostrarMensagem("Mensagem: " + resultado.getMensagem());
    }
    
    private void testarAvaliacaoNotaInvalida() {
        output.mostrarMensagem("\n❌ CENÁRIO 5: Registro de avaliação com nota inválida");
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarErro("Não há turmas para testar.");
            return;
        }
        
        Turma turma = turmas.get(0);
        if (turma.getQuantidadeAlunos() == 0) {
            output.mostrarErro("A turma não tem alunos matriculados.");
            return;
        }
        
        Aluno aluno = turma.getAlunos().get(0);
        
        output.mostrarMensagem(String.format("Tentando registrar avaliação com nota 15 (inválida) para aluno '%s'", aluno.getNome()));
        
        ResultadoOperacao resultado = service.registrarAvaliacao(
            turma.getCodigo(),
            aluno.getMatricula(),
            "Prova Teste",
            "Prova",
            0.3,
            15.0 // Nota inválida (> 10)
        );
        
        if (!resultado.isSucesso()) {
            output.mostrarSucesso("✅ TESTE PASSOU (esperava falha): " + resultado.getMensagem());
        } else {
            output.mostrarErro("❌ TESTE FALHOU: Registro não deveria ter sucesso!");
        }
    }
    
    private void testarMatriculaDuplicada() {
        output.mostrarMensagem("\n❌ CENÁRIO 6: Matrícula de aluno já matriculado");
        
        List<Turma> turmas = service.getTurmaRepository().listarTodos();
        if (turmas.isEmpty()) {
            output.mostrarErro("Não há turmas para testar.");
            return;
        }
        
        Turma turma = turmas.get(0);
        if (turma.getQuantidadeAlunos() == 0) {
            output.mostrarErro("A turma não tem alunos matriculados.");
            return;
        }
        
        Aluno aluno = turma.getAlunos().get(0);
        
        output.mostrarMensagem(String.format("Tentando matricular aluno '%s' que já está na turma '%s'", 
            aluno.getNome(), turma.getCodigo()));
        
        // Tentar matricular novamente
        turma.adicionarAluno(aluno); // Isso não deveria adicionar duplicado
        service.getTurmaRepository().salvar(turma);
        
        long quantidadeDuplicados = turma.getAlunos().stream()
            .filter(a -> a.getMatricula().equals(aluno.getMatricula()))
            .count();
        
        if (quantidadeDuplicados == 1) {
            output.mostrarSucesso("✅ TESTE PASSOU: Aluno não foi duplicado na turma");
        } else {
            output.mostrarErro("❌ TESTE FALHOU: Aluno foi duplicado na turma!");
        }
    }
    
    private void sair() {
        output.mostrarMensagem("\nObrigado por usar o Sistema de Gestão Educacional!");
        output.mostrarMensagem("Desenvolvido por: Jean Ricardo Land Miranda");
        output.mostrarMensagem("RA: 24231215-5");
        executando = false;
    }
}

// ==================== MAIN APPLICATION ====================
public class SistemaGestaoEducacional {
    public static void main(String[] args) {
        // Configurar dependências (Dependency Injection)
        Logger logger = new ConsoleLogger();
        OutputHandler output = new ConsoleOutputHandler(logger);
        InputHandler input = new ConsoleInputHandler();
        
        // Criar serviços
        SistemaEducacionalService service = new SistemaEducacionalService(logger);
        
        // Criar e iniciar controlador do menu
        MenuController menuController = new MenuController(service, output, input);
        
        try {
            menuController.iniciar();
        } finally {
            if (input instanceof ConsoleInputHandler) {
                ((ConsoleInputHandler) input).fechar();
            }
        }
    }
}