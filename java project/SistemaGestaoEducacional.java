package javacode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
}

class Avaliacao {
    private double nota;
    private final String descricao;
    
    public Avaliacao(String descricao) {
        this.descricao = descricao;
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
    
    private String hashSenha(String senha) {
        // Em um sistema real, usar BCrypt ou similar
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
            sb.append("\nAvaliações:\n");
            for (Avaliacao av : avaliacoes) {
                sb.append(String.format("- %s: %.1f\n", av.getDescricao(), av.getNota()));
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DO ALUNO - %s", aluno.getNome());
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
}

class RelatorioCurso implements Relatorio {
    private final Curso curso;
    
    public RelatorioCurso(Curso curso) {
        this.curso = curso;
    }
    
    @Override
    public String gerarConteudo() {
        return String.format(
            "Nome: %s\nCódigo: %s\nCarga Horária: %d horas\nTipo: %s",
            curso.getNome(), curso.getCodigo(), curso.getCargaHoraria(),
            curso instanceof CursoPresencial ? "Presencial" : 
            curso instanceof CursoEAD ? "EAD" : "Regular"
        );
    }
    
    @Override
    public String getTitulo() {
        return String.format("RELATÓRIO DO CURSO - %s", curso.getNome());
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
        
        // Verificar se aluno já está matriculado em alguma turma deste curso
        if (turmaRepository.existeTurmaComCurso(codigoCurso)) {
            // Em um sistema real, verificaríamos se o aluno específico já está matriculado
            logger.info(String.format("Aluno %s sendo verificado para matrícula no curso %s", 
                                     matriculaAluno, codigoCurso));
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
    private final ValidadorSistema validador;
    private final Logger logger;
    
    public SistemaEducacionalService(Logger logger) {
        this.logger = logger;
        this.alunoRepository = new AlunoRepository(logger);
        this.usuarioRepository = new UsuarioRepository(logger);
        this.cursoRepository = new CursoRepository(logger);
        this.turmaRepository = new TurmaRepository(logger);
        this.validador = new ValidadorSistema(cursoRepository, alunoRepository, turmaRepository, logger);
    }
    
    public void carregarDadosIniciais() {
        // Primeiro criar os cursos
        Curso curso = new Curso("Java OO", "JAVA101", 60);
        cursoRepository.salvar(curso);
        
        CursoPresencial cursoPresencial = new CursoPresencial("Java Avançado", "JAVA201", 80, "Sala 301");
        cursoRepository.salvar(cursoPresencial);
        
        CursoEAD cursoEAD = new CursoEAD("Python", "PY101", 40, "Plataforma Virtual");
        cursoRepository.salvar(cursoEAD);
        
        // Depois criar alunos (agora os cursos existem)
        Aluno aluno = new Aluno("Jean Ricardo Land Miranda", "24231215-5", 
                               "Java OO"); // Curso que existe
        alunoRepository.salvar(aluno);
        
        AlunoAutenticavel alunoAuth = new AlunoAutenticavel("Jean", "jean@edu.com", 
                                                          "24231215-5", "jean", "123");
        usuarioRepository.salvar(alunoAuth);
        
        Professor professor = new Professor("Carlos", "POO", "PROF001");
        ProfessorAutenticavel profAuth = new ProfessorAutenticavel("Carlos", "carlos@edu.com",
                                                                  "POO", "PROF001", "carlos", "456");
        usuarioRepository.salvar(profAuth);
        
        Administrador admin = new Administrador("Admin", "admin@edu.com", "admin", "admin123");
        usuarioRepository.salvar(admin);
        
        // Criar turma (agora curso e professor existem)
        Turma turma = new Turma("TURMA2024", professor, curso);
        turma.adicionarAluno(aluno);
        
        // Adicionar outro aluno em um curso que existe
        Aluno aluno2 = new Aluno("Maria", "002", "Java OO");
        alunoRepository.salvar(aluno2);
        turma.adicionarAluno(aluno2);
        
        turmaRepository.salvar(turma);
        
        logger.sucesso("Dados iniciais carregados com sucesso");
    }
    
    public ResultadoOperacao matricularAlunoEmTurma(String matriculaAluno, String codigoTurma) {
        try {
            // Validar antes de qualquer operação
            Aluno aluno = alunoRepository.buscarPorId(matriculaAluno);
            Turma turma = turmaRepository.buscarPorId(codigoTurma);
            
            if (aluno == null) {
                return ResultadoOperacao.erro("Aluno não encontrado");
            }
            
            if (turma == null) {
                return ResultadoOperacao.erro("Turma não encontrada");
            }
            
            // Validar se o aluno pode ser matriculado neste curso
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
            // Validar dados do aluno
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
            // Validar se código já existe
            if (cursoRepository.existe(codigo)) {
                return ResultadoOperacao.erro(String.format("Curso com código '%s' já existe", codigo));
            }
            
            // Validar se nome já existe
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
            // Validar criação da turma
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
        
        logger.info(String.format("Gerados %d relatórios", relatorios.size()));
        return relatorios;
    }
    
    // Getters para os repositórios
    public AlunoRepository getAlunoRepository() { return alunoRepository; }
    public UsuarioRepository getUsuarioRepository() { return usuarioRepository; }
    public CursoRepository getCursoRepository() { return cursoRepository; }
    public TurmaRepository getTurmaRepository() { return turmaRepository; }
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
        // Em produção, poderia ser desabilitado
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
        // Limpar console (depende do sistema operacional)
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback: imprimir várias linhas em branco
            for (int i = 0; i < 50; i++) System.out.println();
        }
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
            5. Gerar Relatórios
            6. Autenticar Usuário
            7. Estatísticas do Sistema
            8. Mostrar Dados Iniciais
            9. Validar Curso
            0. Sair
            """;
        
        output.mostrarMenu(menu);
        int opcao = input.lerInt("");
        
        switch (opcao) {
            case 1 -> gerenciarAlunos();
            case 2 -> gerenciarProfessores();
            case 3 -> gerenciarCursos();
            case 4 -> gerenciarTurmas();
            case 5 -> gerarRelatorios();
            case 6 -> autenticarUsuario();
            case 7 -> mostrarEstatisticas();
            case 8 -> mostrarDadosIniciais();
            case 9 -> validarCurso();
            case 0 -> sair();
            default -> output.mostrarErro("Opção inválida!");
        }
    }
    
    private void gerenciarAlunos() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE ALUNOS");
        
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
        
        boolean adicionar = input.lerBoolean("Deseja adicionar um novo aluno?", 
                                           "Iniciando cadastro...", "Retornando ao menu...");
        if (adicionar) {
            cadastrarAluno();
        }
    }
    
    private void cadastrarAluno() {
        output.mostrarMensagem("\n📝 CADASTRO DE NOVO ALUNO");
        
        // Mostrar cursos disponíveis primeiro
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
        
        // Selecionar curso válido
        int cursoIndex;
        do {
            cursoIndex = input.lerInt("Número do curso (1-" + cursos.size() + ")") - 1;
            if (cursoIndex < 0 || cursoIndex >= cursos.size()) {
                output.mostrarErro("Número de curso inválido!");
            }
        } while (cursoIndex < 0 || cursoIndex >= cursos.size());
        
        String cursoSelecionado = cursos.get(cursoIndex).getNome();
        
        // Usar o serviço para cadastrar (que já valida)
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
    }
    
    private void gerenciarProfessores() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE PROFESSORES");
        
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
    }
    
    private void gerenciarCursos() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE CURSOS");
        
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
    }
    
    private void gerenciarTurmas() {
        output.limparTela();
        output.mostrarMensagem("📋 GERENCIAMENTO DE TURMAS");
        
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
        
        boolean criar = input.lerBoolean("Deseja criar uma nova turma?", 
                                        "Iniciando criação...", "Retornando ao menu...");
        if (criar) {
            criarTurma();
        }
    }
    
    private void criarTurma() {
        output.mostrarMensagem("\n🏫 CRIAÇÃO DE NOVA TURMA");
        
        // Verificar se temos cursos disponíveis
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        if (cursos.isEmpty()) {
            output.mostrarErro("Não é possível criar turma. Nenhum curso cadastrado.");
            output.mostrarMensagem("Cadastre um curso primeiro na opção 3 do menu.");
            return;
        }
        
        // Verificar se temos professores disponíveis
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
        
        // Selecionar curso
        output.mostrarMensagem("\nSelecione o curso:");
        for (int i = 0; i < cursos.size(); i++) {
            output.mostrarMensagem(String.format("%d. %s", i + 1, cursos.get(i).getNome()));
        }
        
        int indexCurso = input.lerOpcao("Número do curso", 1, cursos.size()) - 1;
        Curso cursoSelecionado = cursos.get(indexCurso);
        
        // Selecionar professor
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
        
        // Usar o serviço para criar turma (que já valida)
        ResultadoOperacao resultado = service.criarTurma(codigoTurma, professor, cursoSelecionado);
        
        if (resultado.isSucesso()) {
            Turma turmaCriada = (Turma) resultado.getDados();
            output.mostrarSucesso(resultado.getMensagem());
            
            // Matricular alunos
            boolean matricular = input.lerBoolean("Deseja matricular alunos agora?", 
                                                "Iniciando matrículas...", "Pulando matrículas...");
            if (matricular) {
                matricularAlunosNaTurma(turmaCriada);
            }
            
            // Mostrar resumo
            output.mostrarMensagem("\n📊 RESUMO DA TURMA CRIADA:");
            output.mostrarMensagem("Código: " + turmaCriada.getCodigo());
            output.mostrarMensagem("Professor: " + turmaCriada.getProfessor().getNome());
            output.mostrarMensagem("Curso: " + turmaCriada.getCurso().getNome());
            output.mostrarMensagem("Quantidade de alunos: " + turmaCriada.getQuantidadeAlunos());
        } else {
            output.mostrarErro(resultado.getMensagem());
        }
    }
    
    private void matricularAlunosNaTurma(Turma turma) {
        List<Aluno> alunos = service.getAlunoRepository().listarTodos();
        if (alunos.isEmpty()) {
            output.mostrarMensagem("Nenhum aluno disponível para matrícula.");
            return;
        }
        
        output.mostrarMensagem("Alunos disponíveis:");
        for (int i = 0; i < alunos.size(); i++) {
            Aluno aluno = alunos.get(i);
            // Verificar se o aluno já está na turma
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
                break; // Usuário escolheu 0 para cancelar
            }
            
            Aluno alunoSelecionado = alunos.get(indexAluno);
            
            // Verificar se aluno já está matriculado
            boolean jaMatriculado = turma.getAlunos().stream()
                .anyMatch(a -> a.getMatricula().equals(alunoSelecionado.getMatricula()));
            
            if (jaMatriculado) {
                output.mostrarErro("Este aluno já está matriculado nesta turma!");
            } else {
                // Validar se aluno pode ser matriculado neste curso
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
    
    private void gerarRelatorios() {
        output.limparTela();
        output.mostrarMensagem("📄 GERANDO RELATÓRIOS");
        
        List<Relatorio> relatorios = service.gerarRelatoriosCompletos();
        
        if (relatorios.isEmpty()) {
            output.mostrarMensagem("Nenhum dado disponível para gerar relatórios.");
            return;
        }
        
        output.mostrarMensagem(String.format("Gerados %d relatórios:", relatorios.size()));
        
        for (Relatorio relatorio : relatorios) {
            output.mostrarMensagem("\n" + "=".repeat(40));
            output.mostrarMensagem(relatorio.getTitulo());
            output.mostrarMensagem("=".repeat(40));
            output.mostrarMensagem(relatorio.gerarConteudo());
        }
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void autenticarUsuario() {
        output.limparTela();
        output.mostrarMensagem("🔐 AUTENTICAÇÃO DE USUÁRIO");
        
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
        
        int totalAlunos = service.getAlunoRepository().listarTodos().size();
        int totalUsuarios = service.getUsuarioRepository().listarTodos().size();
        int totalCursos = service.getCursoRepository().listarTodos().size();
        int totalTurmas = service.getTurmaRepository().listarTodos().size();
        
        output.mostrarMensagem(String.format("Total de alunos: %d", totalAlunos));
        output.mostrarMensagem(String.format("Total de usuários: %d", totalUsuarios));
        output.mostrarMensagem(String.format("Total de cursos: %d", totalCursos));
        output.mostrarMensagem(String.format("Total de turmas: %d", totalTurmas));
        
        // Contar tipos de usuários
        List<Usuario> usuarios = service.getUsuarioRepository().listarTodos();
        long alunosAuth = usuarios.stream().filter(u -> u instanceof AlunoAutenticavel).count();
        long professores = usuarios.stream().filter(u -> u instanceof ProfessorAutenticavel).count();
        long admins = usuarios.stream().filter(u -> u instanceof Administrador).count();
        
        output.mostrarMensagem("\nDistribuição de usuários:");
        output.mostrarMensagem(String.format("Alunos autenticáveis: %d", alunosAuth));
        output.mostrarMensagem(String.format("Professores: %d", professores));
        output.mostrarMensagem(String.format("Administradores: %d", admins));
        
        // Informações sobre cursos
        List<Curso> cursos = service.getCursoRepository().listarTodos();
        long cursosPresenciais = cursos.stream().filter(c -> c instanceof CursoPresencial).count();
        long cursosEAD = cursos.stream().filter(c -> c instanceof CursoEAD).count();
        long cursosRegulares = cursos.size() - cursosPresenciais - cursosEAD;
        
        output.mostrarMensagem("\nDistribuição de cursos:");
        output.mostrarMensagem(String.format("Cursos presenciais: %d", cursosPresenciais));
        output.mostrarMensagem(String.format("Cursos EAD: %d", cursosEAD));
        output.mostrarMensagem(String.format("Cursos regulares: %d", cursosRegulares));
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void mostrarDadosIniciais() {
        output.limparTela();
        output.mostrarMensagem("💾 DADOS INICIAIS PRÉ-CARREGADOS");
        output.mostrarMensagem("=".repeat(60));
        
        output.mostrarMensagem("\n🎓 ALUNO DE EXEMPLO:");
        output.mostrarMensagem("- Jean Ricardo Land Miranda");
        output.mostrarMensagem("- RA: 24231215-5");
        output.mostrarMensagem("- Curso: Java OO (curso válido)");
        
        output.mostrarMensagem("\n🔐 CREDENCIAIS PARA TESTE:");
        output.mostrarMensagem("1. Aluno: login='jean', senha='123'");
        output.mostrarMensagem("2. Professor: login='carlos', senha='456'");
        output.mostrarMensagem("3. Administrador: login='admin', senha='admin123'");
        
        output.mostrarMensagem("\n📚 CURSOS DISPONÍVEIS:");
        output.mostrarMensagem("- Java OO (JAVA101) - 60h");
        output.mostrarMensagem("- Java Avançado (JAVA201) - 80h - Presencial - Sala 301");
        output.mostrarMensagem("- Python (PY101) - 40h - EAD - Plataforma Virtual");
        
        output.mostrarMensagem("\n🏫 TURMA PRÉ-CONFIGURADA:");
        output.mostrarMensagem("- Código: TURMA2024");
        output.mostrarMensagem("- Professor: Carlos");
        output.mostrarMensagem("- Curso: Java OO");
        output.mostrarMensagem("- 2 alunos matriculados");
        
        input.lerString("\nPressione Enter para continuar...");
    }
    
    private void validarCurso() {
        output.limparTela();
        output.mostrarMensagem("🔍 VALIDAÇÃO DE CURSO");
        
        output.mostrarMensagem("\nOpções de validação:");
        output.mostrarMensagem("1. Validar por código do curso");
        output.mostrarMensagem("2. Validar por nome do curso");
        output.mostrarMensagem("3. Ver todos os cursos disponíveis");
        
        int opcao = input.lerOpcao("Escolha uma opção", 1, 3);
        
        switch (opcao) {
            case 1 -> {
                String codigo = input.lerString("Código do curso");
                ResultadoValidacao resultado = service.getValidador().validarCursoExiste(codigo);
                if (resultado.isValido()) {
                    output.mostrarSucesso(resultado.getMensagem());
                    // Mostrar informações do curso
                    Curso curso = service.getCursoRepository().buscarPorId(codigo);
                    if (curso != null) {
                        output.mostrarMensagem("\n📋 Informações do curso:");
                        output.mostrarMensagem("Nome: " + curso.getNome());
                        output.mostrarMensagem("Código: " + curso.getCodigo());
                        output.mostrarMensagem("Carga horária: " + curso.getCargaHoraria() + "h");
                    }
                } else {
                    output.mostrarErro(resultado.getMensagem());
                }
            }
            case 2 -> {
                String nome = input.lerString("Nome do curso");
                ResultadoValidacao resultado = service.getValidador().validarCursoPorNomeExiste(nome);
                if (resultado.isValido()) {
                    output.mostrarSucesso(resultado.getMensagem());
                } else {
                    output.mostrarErro(resultado.getMensagem());
                }
            }
            case 3 -> {
                List<Curso> cursos = service.getCursoRepository().listarTodos();
                if (cursos.isEmpty()) {
                    output.mostrarMensagem("Nenhum curso cadastrado no sistema.");
                } else {
                    output.mostrarMensagem("\n📚 CURSOS DISPONÍVEIS NO SISTEMA:");
                    for (Curso curso : cursos) {
                        output.mostrarMensagem("- " + curso.getNome() + " (" + curso.getCodigo() + ")");
                    }
                }
            }
        }
        
        input.lerString("\nPressione Enter para continuar...");
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