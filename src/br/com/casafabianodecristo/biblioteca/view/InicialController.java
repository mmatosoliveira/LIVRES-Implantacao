package br.com.casafabianodecristo.biblioteca.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import br.com.casafabianodecristo.biblioteca.Principal;
import br.com.casafabianodecristo.biblioteca.appservice.*;
import br.com.casafabianodecristo.biblioteca.dto.InicialDto;
import br.com.casafabianodecristo.biblioteca.model.*;
import br.com.casafabianodecristo.biblioteca.utils.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.control.*;

public class InicialController {
	@FXML
	private Button iconePesquisar;
	
	@FXML
	private Label dataHora;
	
	@FXML
	private Label usuarioAcesso;
	
	@FXML
	private TableView<Emprestimo> tabelaEmprestimosPendentes;
	
	@FXML
	private TableColumn<Emprestimo, String> colunaNomeUsuario;
	
	@FXML
	private TableColumn<Emprestimo, String> colunaTitulo;
	
	@FXML
	private MenuItem itemSair = new MenuItem("Close");
	
	@FXML
	private MenuItem itemRealizarBackup;
	
	@FXML
	private MenuItem itemGerarEtiquetas;
	
	@FXML
	private MenuItem itemGerenciarClassificacoes;
	
	@FXML
	private MenuItem itemMeusDados;
	
	@FXML
	private MenuItem itemCadastrarLivros;
	
	@FXML
	private MenuItem itemCadastrarUsuario;
	
	@FXML
	private MenuItem itemEditarDadosUsuario;
	
	@FXML
	private MenuItem itemCadastrarClassificacao;
	
	@FXML
	private MenuItem itemRemoverDoarLivro;
	
	@FXML
	private MenuItem itemConsultarEmprestimo;
	
	@FXML
	private MenuItem itemRealizarEmprestimo;
	
	@FXML
	private MenuItem itemConfigurarImpressoraPadrao;
	
	@FXML
	private TextField dadoLivroPesquisa;
	
	@FXML
	private CheckBox checkTitulo;
	
	@FXML
	private CheckBox checkAutor;
	
	@FXML
	private CheckBox checkTombo;
	
	@FXML
	private Button botaoCadastrarLivro;
	
	@FXML
	private Button botaoCadastrarUsuario;
	
	@FXML
	private Button botaoEmprestarLivro;
	
	@FXML
	private Button botaoRenovarEmprestimo;
	
	@FXML
	private MenuItem itemGerarRelatorio;
	
	@FXML
	private Label labelId;
	
	private List<Livro> livros;
	
	private BibliotecaAppService servico = new BibliotecaAppService();
	
	private Principal principal;
	
	private Alertas alerta = new Alertas();
	
	private Tooltip tpPesquisa = new Tooltip();
	
	public InicialController(){}
	
	private void atualizarGrid(){
		colunaNomeUsuario.setCellValueFactory(x -> new ReadOnlyStringWrapper(x
				.getValue()
				.getUsuario()
				.getNomeCompleto()));
		colunaTitulo.setCellValueFactory(x -> new ReadOnlyStringWrapper(x
					.getValue()
					.getLivro()
					.getTitulo()));
	}
	
	@FXML
	private void initialize(){
		botaoCadastrarLivro.getStylesheets().add(EmprestarLivroController.class.getResource("style.css").toExternalForm());
		botaoCadastrarUsuario.getStylesheets().add(EmprestarLivroController.class.getResource("style.css").toExternalForm());
		botaoEmprestarLivro.getStylesheets().add(EmprestarLivroController.class.getResource("style.css").toExternalForm());
		botaoRenovarEmprestimo.getStylesheets().add(EmprestarLivroController.class.getResource("style.css").toExternalForm());
		iconePesquisar.getStylesheets().add(EmprestarLivroController.class.getResource("style.css").toExternalForm());
		tpPesquisa.setText("Digite alguma informação e pesquise um livro!");
		dadoLivroPesquisa.setTooltip(tpPesquisa);
		
		atualizarGrid();	
		
		dadoLivroPesquisa.setOnKeyPressed((new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER){
					consultarLivro();
				}				
			}
		}));
		
		itemGerarRelatorio.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				principal.carregarGerenciamentoRelatorios();
			}
		});
		
		itemGerarEtiquetas.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				GeradorDeRelatorios gerador = new GeradorDeRelatorios("TodosOsLivros.jrxml");
				try {
					gerador.gerarPdf(new HashMap<String,Object>(), "TodosLivros.pdf");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//principal.carregarGerenciamentoRelatorios();
			}
		});
		
		itemCadastrarClassificacao.setOnAction((e) -> principal.carregarTelaCadastro("CadastrarClassificacao", "Cadastrar classificação de livros", true, false, new TableView<>()));
		
		itemRealizarBackup.setOnAction(e -> principal.carregarTela("RealizarBackup", "Realizar backup", "backup-icon", false, true));
		
		itemCadastrarLivros.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				principal.carregarCadastroLivros();			
			}
		});
		
		itemGerenciarClassificacoes.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				principal.carregarGerenciamentoClassificacoes();			
			}
		});
		
		itemRemoverDoarLivro.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				principal.carregarRemoverDoarLivro();			
			}
		});
		
		itemConsultarEmprestimo.setOnAction((event) -> principal.carregarTela("Emprestimo", "Consultar empréstimos", "icon-search", false, false));
		
		itemCadastrarUsuario.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				principal.carregarTelaCadastro("CadastrarUsuario", "Cadastrar usuário", true, false, new TableView<>());
			}
		});
		
		itemEditarDadosUsuario.setOnAction(e -> principal.carregarTela("BuscarUsuarios", "Gerenciar usuários", "icon-manage", false, true));
		
		itemSair.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				if (alerta.alertaConfirmacaoSair().get() == ButtonType.OK)
					System.exit(0);				
			}
		});		
		
		itemMeusDados.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				InicialDto dto = (InicialDto) botaoCadastrarLivro.getScene().getRoot().getUserData();
				principal.carregarEditarDadosAcesso(dto.getUsuario());		
			}
		});		
		
		itemRealizarEmprestimo.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				principal.carregarEmprestimoLivro();			
			}
		});		
		
		iconePesquisar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	  consultarLivro();	
            }            
        });		
		
		botaoCadastrarLivro.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	principal.carregarCadastroLivros();	
            }            
        });
		
		botaoCadastrarUsuario.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	principal.carregarCadastroUsuario();	
            }            
        });
		
		botaoEmprestarLivro.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	principal.carregarEmprestimoLivro();	
            }            
        });
		
		itemConfigurarImpressoraPadrao.setOnAction((event) -> principal.carregarConfiguracaoImpressora());
	}
	
	public void consultarLivro(){
		String textoPesquisa = dadoLivroPesquisa.getText();
    	String textoAlerta = "Uma pesquisa vazia retorna todos os livros do sistema e isso pode demorar. Deseja continuar?";
    	
    	if (textoPesquisa.equals("")){
    		if(alerta.alertaConfirmacao(textoAlerta).get() == ButtonType.OK){	
    			livros = servico.pesquisaRapidaLivro(textoPesquisa, false);
    			principal.carregarResultadoBusca(livros);
        	}     
    	}
    	else{
			livros = servico.pesquisaRapidaLivro(textoPesquisa, false);
			principal.carregarResultadoBusca(livros);
    	}
	}
	
	public void setPrincipal (Principal principal){
		this.principal = principal;
	}
}
