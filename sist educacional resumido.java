// ==================== FASE FINAL - SISTEMA CONSOLIDADO ====================

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// ==================== ENUMS E INTERFACES ====================
enum TipoCurso { PRESENCIAL, EAD, REGULAR }
enum TipoAvaliacao { PROVA, TRABALHO, SEMINARIO, OUTRO }
enum Permissao { VISUALIZAR, EDITAR, ADMIN }

interface Autenticavel {
    boolean autenticar(String login, String senha);
    boolean temPermissao(Permissao permissao);
}

// ==================== CLASSES MODEL ====================
class Usuario implements Autenticavel {
    protected String nome, email, login, senha;
    protected Set<Permissao> permissoes = new HashSet<>();
    
    public Usuario(String nome, String email, String login, String senha) {
        this.nome = nome; this.email = email; this.login = login; this.senha = senha;
        this.permissoes.add(Permissao.VISUALIZAR);
    }
    
    @Override public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha);
    }
    
    @Override public boolean temPermissao(Permissao permissao) {
        return permissoes.contains(permissao);
    }
    
    public void addPermissao(Permissao p) { permissoes.add(p); }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getLogin() { return login; }
}

class Aluno extends Usuario {
    private String matricula, curso;
    private List<Avaliacao> avaliacoes = new ArrayList<>();
    
    public Aluno(String nome, String email, String login, String senha, 
                 String matricula, String curso) {
        super(nome, email, login, senha);
        this.matricula = matricula;
        this.curso = curso;
    }
    
    public Aluno(String nome, String matricula, String curso) {
        super(nome, nome.toLowerCase().replace(" ", ".") + "@edu.com", 
              matricula, "senha123");
        this.matricula = matricula;
        this.curso = curso;
    }
    
    public void addAvaliacao(Avaliacao av) { avaliacoes.add(av); }
    public double calcularMedia() {
        return avaliacoes.stream().mapToDouble(a -> a.nota * a.peso).sum() /
               avaliacoes.stream().mapToDouble(a -> a.peso).sum();
    }
    public String getMatricula() { return matricula; }
    public String getCurso() { return curso; }
    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    
    @Override public String toString() {
        return nome + " - " + matricula + " - " + curso;
    }
}

class Professor extends Usuario {
    private String especialidade, registro;
    
    public Professor(String nome, String email, String login, String senha,
                     String especialidade, String registro) {
        super(nome, email, login, senha);
        this.especialidade = especialidade;
        this.registro = registro;
        this.addPermissao(Permissao.EDITAR);
    }
    
    public Professor(String nome, String especialidade, String registro) {
        this(nome, nome.toLowerCase().replace(" ", ".") + "@edu.com",
             nome.split(" ")[0].toLowerCase(), "senha123",
             especialidade, registro);
    }
    
    public String getEspecialidade() { return especialidade; }
    public String getRegistro() { return registro; }
    
    @Override public String toString() {
        return nome + " - " + registro + " - " + especialidade;
    }
}

class Curso {
    private String nome, codigo;
    private int cargaHoraria;
    private TipoCurso tipo;
    private String infoEspecifica; // sala ou plataforma
    
    public Curso(String nome, String codigo, int cargaHoraria, 
                 TipoCurso tipo, String infoEspecifica) {
        this.nome = nome; this.codigo = codigo; 
        this.cargaHoraria = cargaHoraria; this.tipo = tipo;
        this.infoEspecifica = infoEspecifica;
    }
    
    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
    public TipoCurso getTipo() { return tipo; }
    
    @Override public String toString() {
        String tipoStr = tipo == TipoCurso.PRESENCIAL ? "Presencial" :
                        tipo == TipoCurso.EAD ? "EAD" : "Regular";
        String info = tipo == TipoCurso.PRESENCIAL ? "Sala: " : "Plataforma: ";
        return nome + " (" + codigo + ") | " + cargaHoraria + "h | " + 
               tipoStr + " | " + info + infoEspecifica;
    }
}

class Turma {
    private String codigo;
    private Curso curso;
    private Professor professor;
    private List<Aluno> alunos = new ArrayList<>();
    private List<Avaliacao> avaliacoes = new ArrayList<>();
    
    public Turma(String codigo, Curso curso, Professor professor) {
        this.codigo = codigo; this.curso = curso; this.professor = professor;
    }
    
    public void matricularAluno(Aluno aluno) { alunos.add(aluno); }
    public void addAvaliacao(Avaliacao av) { avaliacoes.add(av); }
    
    public String getCodigo() { return codigo; }
    public Curso getCurso() { return curso; }
    public Professor getProfessor() { return professor; }
    public List<Aluno> getAlunos() { return alunos; }
    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    
    @Override public String toString() {
        return "Código: " + codigo + " | Curso: " + curso.getNome() + 
               " | Professor: " + professor.getNome() + " | Alunos: " + alunos.size();
    }
}

class Avaliacao {
    String descricao, tipo, data;
    double nota, peso;
    Aluno aluno;
    Turma turma;
    
    public Avaliacao(String descricao, String tipo, double peso, double nota,
                     Aluno aluno, Turma turma) {
        this.descricao = descricao; this.tipo = tipo; this.peso = peso;
        this.nota = nota; this.aluno = aluno; this.turma = turma;
        this.data = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    @Override public String toString() {
        return descricao + " | " + tipo + " | Nota: " + nota + 
               " | Peso: " + peso + " | Aluno: " + aluno.getNome();
    }
}

// ==================== REPOSITORIES ====================
class Repositorio<T> {
    protected List<T> itens = new ArrayList<>();
    
    public void salvar(T item) { itens.add(item); }
    public List<T> listarTodos() { return new ArrayList<>(itens); }
    public Optional<T> buscar(int index) {
        return index >= 0 && index < itens.size() ? Optional.of(itens.get(index)) : Optional.empty();
    }
}

class AlunoRepo extends Repositorio<Aluno> {
    public Optional<Aluno> buscarPorMatricula(String matricula) {
        return itens.stream().filter(a -> a.getMatricula().equals(matricula)).findFirst();
    }
}

class ProfessorRepo extends Repositorio<Professor> {}
class CursoRepo extends Repositorio<Curso> {}
class TurmaRepo extends Repositorio<Turma> {
    public Optional<Turma> buscarPorCodigo(String codigo) {
        return itens.stream().filter(t -> t.getCodigo().equals(codigo)).findFirst();
    }
}
class UsuarioRepo extends Repositorio<Usuario> {
    public Optional<Usuario> buscarPorLogin(String login) {
        return itens.stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }
}
class AvaliacaoRepo extends Repositorio<Avaliacao> {}

// ==================== SISTEMA PRINCIPAL ====================
public class SistemaEducacionalConsolidado {
    private static Scanner sc = new Scanner(System.in);
    private static AlunoRepo alunoRepo = new AlunoRepo();
    private static ProfessorRepo professorRepo = new ProfessorRepo();
    private static CursoRepo cursoRepo = new CursoRepo();
    private static TurmaRepo turmaRepo = new TurmaRepo();
    private static UsuarioRepo usuarioRepo = new UsuarioRepo();
    private static AvaliacaoRepo avaliacaoRepo = new AvaliacaoRepo();
    private static Usuario usuarioLogado = null;
    
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTÃO EDUCACIONAL - EDUCONNECT ===");
        System.out.println("Aluno: Jean Ricardo Land Miranda");
        System.out.println("RA: 24231215-5");
        System.out.println("Curso: Análise e Desenvolvimento de Sistemas");
        System.out.println("=================================================\n");
        
        carregarDadosIniciais();
        
        int opcao;
        do {
            exibirMenuPrincipal();
            System.out.print("\nEscolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();
            
            switch (opcao) {
                case 1 -> gerenciarAlunos();
                case 2 -> gerenciarProfessores();
                case 3 -> gerenciarCursos();
                case 4 -> gerenciarTurmas();
                case 5 -> registrarAvaliacoes();
                case 6 -> gerarRelatorios();
                case 7 -> autenticarUsuario();
                case 8 -> exibirEstatisticas();
                case 9 -> testarCenarios();
                case 0 -> System.out.println("Encerrando sistema...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
        sc.close();
    }
    
    private static void carregarDadosIniciais() {
        // Cursos
        cursoRepo.salvar(new Curso("Java OO", "JAVA101", 60, TipoCurso.PRESENCIAL, "Sala 101"));
        cursoRepo.salvar(new Curso("Java Avançado", "JAVA201", 80, TipoCurso.PRESENCIAL, "Sala 301"));
        cursoRepo.salvar(new Curso("Python", "PY101", 40, TipoCurso.EAD, "Plataforma Virtual"));
        
        // Alunos
        alunoRepo.salvar(new Aluno("Jean Ricardo Land Miranda", "24231215-5", "Java OO"));
        alunoRepo.salvar(new Aluno("Maria Silva", "002", "Java OO"));
        alunoRepo.salvar(new Aluno("Pedro Santos", "003", "Python"));
        
        // Usuários
        usuarioRepo.salvar(new Usuario("Jean", "jean@edu.com", "jean", "123"));
        usuarioRepo.salvar(new Usuario("Carlos", "carlos@edu.com", "carlos", "123"));
        Usuario admin = new Usuario("Admin", "admin@edu.com", "admin", "admin123");
        admin.addPermissao(Permissao.ADMIN);
        usuarioRepo.salvar(admin);
        
        // Professor
        professorRepo.salvar(new Professor("Carlos", "Desenvolvimento", "P00"));
        
        // Turma
        Turma turma = new Turma("TURMA2024", cursoRepo.listarTodos().get(0), professorRepo.listarTodos().get(0));
        turma.matricularAluno(alunoRepo.listarTodos().get(0));
        turma.matricularAluno(alunoRepo.listarTodos().get(1));
        turmaRepo.salvar(turma);
        
        System.out.println("Dados iniciais carregados com sucesso!\n");
    }
    
    private static void exibirMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Gerenciar Alunos");
        System.out.println("2. Gerenciar Professores");
        System.out.println("3. Gerenciar Cursos");
        System.out.println("4. Gerenciar Turmas");
        System.out.println("5. Registrar Avaliações");
        System.out.println("6. Gerar Relatórios");
        System.out.println("7. Autenticar Usuário");
        System.out.println("8. Estatísticas do Sistema");
        System.out.println("9. Testar Cenários");
        System.out.println("0. Sair");
    }
    
    private static void gerenciarAlunos() {
        System.out.println("\n=== GERENCIAMENTO DE ALUNOS ===");
        System.out.println("Alunos cadastrados:");
        List<Aluno> alunos = alunoRepo.listarTodos();
        for (int i = 0; i < alunos.size(); i++) {
            System.out.println((i+1) + ". " + alunos.get(i));
        }
        System.out.println("-----------------------");
        
        System.out.print("Deseja adicionar um novo aluno? (S/N): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            System.out.println("\n=== CADASTRO DE NOVO ALUNO ===");
            System.out.println("CURSOS DISPONÍVEIS:");
            List<Curso> cursos = cursoRepo.listarTodos();
            for (int i = 0; i < cursos.size(); i++) {
                System.out.println((i+1) + ". " + cursos.get(i).getNome());
            }
            
            System.out.print("\nNome completo: ");
            String nome = sc.nextLine();
            System.out.print("Matrícula: ");
            String matricula = sc.nextLine();
            System.out.print("Número do curso (1-" + cursos.size() + "): ");
            int cursoIdx = sc.nextInt() - 1;
            sc.nextLine();
            
            if (cursoIdx >= 0 && cursoIdx < cursos.size()) {
                Aluno novoAluno = new Aluno(nome, matricula, cursos.get(cursoIdx).getNome());
                alunoRepo.salvar(novoAluno);
                System.out.println("Aluno " + nome + " cadastrado com sucesso!");
                
                System.out.print("Deseja criar usuário autenticável? (S/N): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    System.out.print("Email: "); String email = sc.nextLine();
                    System.out.print("Login: "); String login = sc.nextLine();
                    System.out.print("Senha: "); String senha = sc.nextLine();
                    
                    Usuario user = new Usuario(nome, email, login, senha);
                    usuarioRepo.salvar(user);
                    System.out.println("Usuário autenticável criado com sucesso!");
                }
            }
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void gerenciarProfessores() {
        System.out.println("\n=== GERENCIAMENTO DE PROFESSORES ===");
        System.out.println("Professores cadastrados:");
        List<Professor> professores = professorRepo.listarTodos();
        for (int i = 0; i < professores.size(); i++) {
            System.out.println((i+1) + ". " + professores.get(i));
        }
        System.out.println("-----------------------");
        
        System.out.print("Deseja adicionar um novo professor? (S/N): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            System.out.println("\n=== CADASTRO DE NOVO PROFESSOR ===");
            System.out.print("Nome completo: "); String nome = sc.nextLine();
            System.out.print("Especialidade: "); String esp = sc.nextLine();
            System.out.print("Registro: "); String reg = sc.nextLine();
            System.out.print("Email: "); String email = sc.nextLine();
            System.out.print("Login: "); String login = sc.nextLine();
            System.out.print("Senha: "); String senha = sc.nextLine();
            
            Professor novoProf = new Professor(nome, email, login, senha, esp, reg);
            professorRepo.salvar(novoProf);
            usuarioRepo.salvar(novoProf);
            System.out.println("Professor cadastrado com sucesso!");
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void gerenciarCursos() {
        System.out.println("\n=== GERENCIAMENTO DE CURSOS ===");
        System.out.println("Cursos cadastrados:");
        List<Curso> cursos = cursoRepo.listarTodos();
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println((i+1) + ". " + cursos.get(i));
        }
        System.out.println("-----------------------");
        
        System.out.print("Deseja adicionar um novo curso? (S/N): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            System.out.println("\n=== CADASTRO DE NOVO CURSO ===");
            System.out.print("Nome do curso: "); String nome = sc.nextLine();
            System.out.print("Código: "); String codigo = sc.nextLine();
            System.out.print("Carga horária: "); int carga = sc.nextInt();
            sc.nextLine();
            
            System.out.println("\nTipo de curso:");
            System.out.println("1. Presencial");
            System.out.println("2. EAD");
            System.out.println("3. Regular");
            System.out.print("Escolha (1-3): ");
            int tipo = sc.nextInt();
            sc.nextLine();
            
            String info = "";
            if (tipo == 1) {
                System.out.print("Sala: "); info = sc.nextLine();
            } else if (tipo == 2) {
                System.out.print("Plataforma: "); info = sc.nextLine();
            }
            
            TipoCurso tipoCurso = tipo == 1 ? TipoCurso.PRESENCIAL : 
                                 tipo == 2 ? TipoCurso.EAD : TipoCurso.REGULAR;
            
            Curso novoCurso = new Curso(nome, codigo, carga, tipoCurso, info);
            cursoRepo.salvar(novoCurso);
            System.out.println("Curso cadastrado com sucesso!");
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void gerenciarTurmas() {
        System.out.println("\n=== GERENCIAMENTO DE TURMAS ===");
        System.out.println("Turmas cadastradas:");
        List<Turma> turmas = turmaRepo.listarTodos();
        for (Turma t : turmas) {
            System.out.println(t);
        }
        System.out.println("-----------------------");
        
        System.out.println("Opções:");
        System.out.println("1. Criar nova turma");
        System.out.println("2. Matricular aluno em turma existente");
        System.out.println("3. Voltar");
        System.out.print("Escolha (1-3): ");
        int op = sc.nextInt();
        sc.nextLine();
        
        if (op == 1) {
            System.out.println("\n=== CRIAÇÃO DE NOVA TURMA ===");
            System.out.print("Código da turma: "); String codigo = sc.nextLine();
            
            System.out.println("\nSelecione o curso:");
            List<Curso> cursos = cursoRepo.listarTodos();
            for (int i = 0; i < cursos.size(); i++) {
                System.out.println((i+1) + ". " + cursos.get(i).getNome());
            }
            System.out.print("Número do curso (1-" + cursos.size() + "): ");
            int cursoIdx = sc.nextInt() - 1;
            sc.nextLine();
            
            System.out.println("\nSelecione o professor:");
            List<Professor> professores = professorRepo.listarTodos();
            for (int i = 0; i < professores.size(); i++) {
                System.out.println((i+1) + ". " + professores.get(i).getNome());
            }
            System.out.print("Número do professor (1-" + professores.size() + "): ");
            int profIdx = sc.nextInt() - 1;
            sc.nextLine();
            
            if (cursoIdx >= 0 && cursoIdx < cursos.size() && 
                profIdx >= 0 && profIdx < professores.size()) {
                
                Turma novaTurma = new Turma(codigo, cursos.get(cursoIdx), professores.get(profIdx));
                turmaRepo.salvar(novaTurma);
                System.out.println("Turma " + codigo + " criada com sucesso!");
                
                System.out.print("Deseja matricular alunos agora? (S/N): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    matricularAlunosTurma(novaTurma);
                }
            }
        } else if (op == 2) {
            System.out.println("\nSelecione a turma:");
            for (int i = 0; i < turmas.size(); i++) {
                System.out.println((i+1) + ". " + turmas.get(i).getCodigo());
            }
            System.out.print("Número da turma (1-" + turmas.size() + "): ");
            int turmaIdx = sc.nextInt() - 1;
            sc.nextLine();
            
            if (turmaIdx >= 0 && turmaIdx < turmas.size()) {
                matricularAlunosTurma(turmas.get(turmaIdx));
            }
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void matricularAlunosTurma(Turma turma) {
        System.out.println("\nAlunos disponíveis:");
        List<Aluno> alunos = alunoRepo.listarTodos();
        for (int i = 0; i < alunos.size(); i++) {
            System.out.println((i+1) + ". " + alunos.get(i));
        }
        
        while (true) {
            System.out.print("\nNúmero do aluno para matricular (0 para cancelar): ");
            int alunoIdx = sc.nextInt() - 1;
            sc.nextLine();
            
            if (alunoIdx == -1) break;
            if (alunoIdx >= 0 && alunoIdx < alunos.size()) {
                turma.matricularAluno(alunos.get(alunoIdx));
                System.out.println("Aluno " + alunos.get(alunoIdx).getNome() + " matriculado!");
            }
            
            System.out.print("Matricular outro aluno? (S/N): ");
            if (!sc.nextLine().equalsIgnoreCase("s")) break;
        }
        
        System.out.println("\n=== RESUMO DA TURMA ===");
        System.out.println("Código: " + turma.getCodigo());
        System.out.println("Professor: " + turma.getProfessor().getNome());
        System.out.println("Curso: " + turma.getCurso().getNome());
        System.out.println("Alunos matriculados: " + turma.getAlunos().size());
    }
    
    private static void registrarAvaliacoes() {
        System.out.println("\n=== GERENCIAMENTO DE AVALIAÇÕES ===");
        System.out.println("Opções:");
        System.out.println("1. Registrar nova avaliação");
        System.out.println("2. Ver avaliações de uma turma");
        System.out.println("3. Ver avaliações de um aluno");
        System.out.println("4. Voltar");
        System.out.print("Escolha (1-4): ");
        int op = sc.nextInt();
        sc.nextLine();
        
        if (op == 1) {
            System.out.println("\n=== REGISTRO DE NOVA AVALIAÇÃO ===");
            System.out.println("Turmas disponíveis:");
            List<Turma> turmas = turmaRepo.listarTodos();
            for (int i = 0; i < turmas.size(); i++) {
                System.out.println((i+1) + ". " + turmas.get(i).getCodigo() + " - " + 
                                   turmas.get(i).getCurso().getNome());
            }
            System.out.print("Selecione a turma (1-" + turmas.size() + "): ");
            int turmaIdx = sc.nextInt() - 1;
            sc.nextLine();
            
            if (turmaIdx >= 0 && turmaIdx < turmas.size()) {
                Turma turma = turmas.get(turmaIdx);
                System.out.println("\nAlunos da turma:");
                List<Aluno> alunosTurma = turma.getAlunos();
                for (int i = 0; i < alunosTurma.size(); i++) {
                    System.out.println((i+1) + ". " + alunosTurma.get(i).getNome());
                }
                System.out.print("Selecione o aluno (1-" + alunosTurma.size() + "): ");
                int alunoIdx = sc.nextInt() - 1;
                sc.nextLine();
                
                if (alunoIdx >= 0 && alunoIdx < alunosTurma.size()) {
                    System.out.print("Descrição da avaliação: ");
                    String descricao = sc.nextLine();
                    System.out.print("Tipo (Prova, Trabalho, etc.): ");
                    String tipo = sc.nextLine();
                    System.out.print("Peso (0.01 a 1.0): ");
                    double peso = sc.nextDouble();
                    System.out.print("Nota (0 a 10): ");
                    double nota = sc.nextDouble();
                    sc.nextLine();
                    
                    Aluno aluno = alunosTurma.get(alunoIdx);
                    Avaliacao av = new Avaliacao(descricao, tipo, peso, nota, aluno, turma);
                    avaliacaoRepo.salvar(av);
                    aluno.addAvaliacao(av);
                    turma.addAvaliacao(av);
                    
                    System.out.println("Avaliação registrada com sucesso!");
                }
            }
        } else if (op == 2) {
            System.out.print("Código da turma: ");
            String codigo = sc.nextLine();
            turmaRepo.buscarPorCodigo(codigo).ifPresentOrElse(
                turma -> {
                    System.out.println("\nAvaliações da turma " + codigo + ":");
                    turma.getAvaliacoes().forEach(System.out::println);
                },
                () -> System.out.println("Turma não encontrada!")
            );
        } else if (op == 3) {
            System.out.print("Matrícula do aluno: ");
            String matricula = sc.nextLine();
            alunoRepo.buscarPorMatricula(matricula).ifPresentOrElse(
                aluno -> {
                    System.out.println("\nAvaliações do aluno " + aluno.getNome() + ":");
                    aluno.getAvaliacoes().forEach(System.out::println);
                },
                () -> System.out.println("Aluno não encontrado!")
            );
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void gerarRelatorios() {
        System.out.println("\n=== GERENCIAMENTO DE RELATÓRIOS ===");
        System.out.println("Opções:");
        System.out.println("1. Relatório Geral do Sistema");
        System.out.println("2. Relatório de Alunos");
        System.out.println("3. Relatório de Professores");
        System.out.println("4. Relatório de Cursos");
        System.out.println("5. Relatório de Turmas");
        System.out.println("6. Relatório de Avaliações");
        System.out.println("7. Todos os Relatórios");
        System.out.println("8. Voltar");
        System.out.print("Escolha (1-8): ");
        int op = sc.nextInt();
        sc.nextLine();
        
        switch (op) {
            case 1 -> relatorioGeral();
            case 2 -> relatorioAlunos();
            case 3 -> relatorioProfessores();
            case 4 -> relatorioCursos();
            case 5 -> relatorioTurmas();
            case 6 -> relatorioAvaliacoes();
            case 7 -> {
                relatorioGeral(); relatorioAlunos(); relatorioProfessores();
                relatorioCursos(); relatorioTurmas(); relatorioAvaliacoes();
            }
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void relatorioTurmas() {
        System.out.println("\n=== RELATÓRIO DE TURMAS ===");
        turmaRepo.listarTodos().forEach(t -> {
            System.out.println("\nTurma: " + t.getCodigo());
            System.out.println("Curso: " + t.getCurso().getNome());
            System.out.println("Professor: " + t.getProfessor().getNome());
            System.out.println("Alunos (" + t.getAlunos().size() + "):");
            t.getAlunos().forEach(a -> System.out.println("  - " + a.getNome() + 
                " (" + a.getMatricula() + ")"));
        });
    }
    
    private static void relatorioGeral() {
        System.out.println("\n=== RELATÓRIO GERAL ===");
        System.out.println("Total de alunos: " + alunoRepo.listarTodos().size());
        System.out.println("Total de professores: " + professorRepo.listarTodos().size());
        System.out.println("Total de cursos: " + cursoRepo.listarTodos().size());
        System.out.println("Total de turmas: " + turmaRepo.listarTodos().size());
        System.out.println("Total de avaliações: " + avaliacaoRepo.listarTodos().size());
    }
    
    private static void relatorioAlunos() {
        System.out.println("\n=== RELATÓRIO DE ALUNOS ===");
        alunoRepo.listarTodos().forEach(a -> {
            System.out.println(a.getNome() + " | Mat: " + a.getMatricula() + 
                " | Curso: " + a.getCurso() + " | Média: " + 
                String.format("%.2f", a.calcularMedia()));
        });
    }
    
    private static void relatorioProfessores() {
        System.out.println("\n=== RELATÓRIO DE PROFESSORES ===");
        professorRepo.listarTodos().forEach(System.out::println);
    }
    
    private static void relatorioCursos() {
        System.out.println("\n=== RELATÓRIO DE CURSOS ===");
        cursoRepo.listarTodos().forEach(System.out::println);
    }
    
    private static void relatorioAvaliacoes() {
        System.out.println("\n=== RELATÓRIO DE AVALIAÇÕES ===");
        avaliacaoRepo.listarTodos().forEach(System.out::println);
    }
    
    private static void autenticarUsuario() {
        System.out.println("\n=== AUTENTICAÇÃO ===");
        System.out.print("Login: "); String login = sc.nextLine();
        System.out.print("Senha: "); String senha = sc.nextLine();
        
        usuarioRepo.buscarPorLogin(login).ifPresentOrElse(
            usuario -> {
                if (usuario.autenticar(login, senha)) {
                    usuarioLogado = usuario;
                    System.out.println("Autenticado como: " + usuario.getNome());
                    System.out.println("Permissões: " + 
                        (usuario.temPermissao(Permissao.ADMIN) ? "ADMIN" :
                         usuario.temPermissao(Permissao.EDITAR) ? "EDITOR" : "VISUALIZADOR"));
                } else {
                    System.out.println("Senha incorreta!");
                }
            },
            () -> System.out.println("Usuário não encontrado!")
        );
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void exibirEstatisticas() {
        System.out.println("\n=== ESTATÍSTICAS DO SISTEMA ===");
        System.out.println("Total de alunos: " + alunoRepo.listarTodos().size());
        System.out.println("Total de usuários: " + usuarioRepo.listarTodos().size());
        System.out.println("Total de cursos: " + cursoRepo.listarTodos().size());
        System.out.println("Total de turmas: " + turmaRepo.listarTodos().size());
        System.out.println("Total de avaliações: " + avaliacaoRepo.listarTodos().size());
        
        long alunosAuth = usuarioRepo.listarTodos().stream()
            .filter(u -> u instanceof Aluno).count();
        long professores = usuarioRepo.listarTodos().stream()
            .filter(u -> u instanceof Professor).count();
        long admins = usuarioRepo.listarTodos().stream()
            .filter(u -> u.temPermissao(Permissao.ADMIN)).count();
        
        System.out.println("\nDistribuição de usuários:");
        System.out.println("Alunos autenticáveis: " + alunosAuth);
        System.out.println("Professores: " + professores);
        System.out.println("Administradores: " + admins);
        
        long presenciais = cursoRepo.listarTodos().stream()
            .filter(c -> c.getTipo() == TipoCurso.PRESENCIAL).count();
        long ead = cursoRepo.listarTodos().stream()
            .filter(c -> c.getTipo() == TipoCurso.EAD).count();
        long regulares = cursoRepo.listarTodos().stream()
            .filter(c -> c.getTipo() == TipoCurso.REGULAR).count();
        
        System.out.println("\nDistribuição de cursos:");
        System.out.println("Cursos presenciais: " + presenciais);
        System.out.println("Cursos EAD: " + ead);
        System.out.println("Cursos regulares: " + regulares);
        
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    
    private static void testarCenarios() {
        System.out.println("\n=== TESTE DE CENÁRIOS ===");
        System.out.println("1. Cadastro completo de aluno com usuário");
        System.out.println("2. Criação de turma com matrículas");
        System.out.println("3. Registro de avaliações");
        System.out.println("4. Autenticação e permissões");
        System.out.print("Escolha cenário (1-4): ");
        int cenario = sc.nextInt();
        sc.nextLine();
        
        switch (cenario) {
            case 1 -> {
                Aluno alunoTeste = new Aluno("Aluno Teste", "TEST001", "Java OO");
                alunoRepo.salvar(alunoTeste);
                System.out.println("Aluno teste cadastrado!");
            }
            case 2 -> {
                if (!cursoRepo.listarTodos().isEmpty() && !professorRepo.listarTodos().isEmpty()) {
                    Turma turmaTeste = new Turma("TURMATESTE", 
                        cursoRepo.listarTodos().get(0), professorRepo.listarTodos().get(0));
                    turmaRepo.salvar(turmaTeste);
                    System.out.println("Turma teste criada!");
                }
            }
            case 3 -> {
                if (!turmaRepo.listarTodos().isEmpty() && !alunoRepo.listarTodos().isEmpty()) {
                    Avaliacao av = new Avaliacao("Prova Teste", "PROVA", 1.0, 8.5,
                        alunoRepo.listarTodos().get(0), turmaRepo.listarTodos().get(0));
                    avaliacaoRepo.salvar(av);
                    System.out.println("Avaliação teste registrada!");
                }
            }
            case 4 -> {
                System.out.println("Testando autenticação com admin/admin123...");
                usuarioRepo.buscarPorLogin("admin").ifPresent(u -> {
                    System.out.println("Usuário encontrado: " + u.getNome());
                    System.out.println("Pode editar? " + u.temPermissao(Permissao.EDITAR));
                    System.out.println("É admin? " + u.temPermissao(Permissao.ADMIN));
                });
            }
        }
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }
}
