package br.com.casafabianodecristo.biblioteca.view;

import java.util.*;
import org.controlsfx.control.MaskerPane;
import org.modelmapper.ModelMapper;
import br.com.casafabianodecristo.biblioteca.appservice.BibliotecaAppService;
import br.com.casafabianodecristo.biblioteca.components.Numberfield;
import br.com.casafabianodecristo.biblioteca.dto.ClassificacaoDto;
import br.com.casafabianodecristo.biblioteca.dto.LivroDto;
import br.com.casafabianodecristo.biblioteca.interfacevalidator.CadastroLivroInterfaceValidator;
import br.com.casafabianodecristo.biblioteca.utils.Alertas;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CadastroLivroController {
	@FXML
	private Button botaoSalvar;
	
	@FXML
	private Button botaoCancelar;
	
	@FXML
	private Numberfield tomboPatrimonial  = new Numberfield();
	
	@FXML
	private TextField titulo;
	
	@FXML
	private TextField subtitulo;
	
	@FXML
	private TextField nomeAutor;
	
	@FXML
	private Numberfield edicao;
	
	@FXML
	private TextField editora;
	
	@FXML
	private ComboBox<ClassificacaoDto> classificacao = new ComboBox<>();
	
	@FXML
	private Numberfield quantidadeExemplares;
	
	private BibliotecaAppService appService = new BibliotecaAppService();
	
	@SuppressWarnings("rawtypes")
	private Task cadastrarLivro;
	
	Alertas alerta = new Alertas();
	
	@FXML
	private MaskerPane avisoCarregando = new MaskerPane();
	
	@FXML
	private BorderPane paneCarregando = new BorderPane();

	public CadastroLivroController(){}
	
	@FXML
	public void initialize(){
		tomboPatrimonial.setEditable(false);
		tomboPatrimonial.setText(appService.getUltimoTombo()+1+"");
		edicao.setMaxLength(3);
		quantidadeExemplares.setMaxLength(2);
		ObservableList<ClassificacaoDto> itens = FXCollections.observableArrayList(appService.getClassificacoes());
		classificacao.setItems(itens);
		
		List<TextField> camposTexto = new ArrayList<TextField>();
		camposTexto.add(tomboPatrimonial);
		camposTexto.add(edicao);
		camposTexto.add(editora);
		camposTexto.add(titulo);
		camposTexto.add(nomeAutor);
		camposTexto.add(quantidadeExemplares);
		
		for (TextField tf : camposTexto){
			tf.focusedProperty().addListener(new ChangeListener<Boolean>()
			{
			    @Override
			    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			    {
			        if (oldPropertyValue && tf.getText().equals(""))
			        	tf.setStyle("-fx-background-color: #ff7c7c;");
			        else if (oldPropertyValue && !tf.getText().equals(""))
			        	tf.setStyle("");
			    }
			});
		}
		
		classificacao.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
		    {
		        if (oldPropertyValue && classificacao.getSelectionModel().getSelectedItem() == null)
		        	classificacao.setStyle("-fx-background-color: #ff7c7c;");
		        else if (oldPropertyValue && classificacao.getSelectionModel().getSelectedItem() != null)
		        	classificacao.setStyle("-fx-background-color: "+ classificacao.getSelectionModel().getSelectedItem().getCor() + ";");
		        	
		    }
		});
		
		botaoCancelar.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
				if(alerta.alertaConfirmacaoSairTelaCadastro().get() == ButtonType.OK){
					Stage stage = (Stage) botaoCancelar.getScene().getWindow();
	            	stage.close();	
				}
			}
		});
		
		botaoSalvar.setOnAction(new EventHandler<ActionEvent>(){
			@Override
            public void handle(ActionEvent event) {
            	if (CadastroLivroInterfaceValidator.validarCamposObrigatorios(camposTexto, classificacao) &&
            		CadastroLivroInterfaceValidator.validarTamanhosMaximos(titulo, subtitulo, nomeAutor, editora, tomboPatrimonial))
            	{
            		mudarComportamentoTela(true);
                	cadastrarLivro = taskCadastrarLivro();
        			Thread t = new Thread(cadastrarLivro);
        			t.setDaemon(true);
        			t.start();
            	}
			}
		});
	}
	
	private void mudarComportamentoTela(boolean estado){
		avisoCarregando.setText("Salvando livro(s)... Aguarde");
		avisoCarregando.setVisible(estado);
		paneCarregando.setVisible(estado);
	}
	
	private boolean cadastrarLivro(){
		int tombo = tomboPatrimonial.getValue();
    	int edicaoLivro = edicao.getValue();
    	int qtdExemplares = quantidadeExemplares.getValue();
    	ModelMapper mapper = new ModelMapper();                	
    	ClassificacaoDto dtoClassif = mapper.map(classificacao.getSelectionModel().getSelectedItem(), ClassificacaoDto.class);
    	LivroDto dto = new LivroDto(tombo, titulo.getText(), subtitulo.getText(), nomeAutor.getText(), editora.getText(), edicaoLivro, dtoClassif, 0, 0, 0);
    	int retorno = appService.cadastrarLivro(dto, qtdExemplares);
    	
		if (retorno == 0)		
			return true;
		else if (retorno == 1)
			return false;
		
		return true;
	};
	
	@SuppressWarnings("rawtypes")
	public Task taskCadastrarLivro() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
        		return cadastrarLivro();
            }
            
            @Override
    		protected void succeeded() {
            	boolean result = (boolean) getValue();
            	if (!result){
            		alerta.notificacaoErro("Cadastrar livro", "Já existe um livro cadastrado com esse tombo patrimonial.\nConfira o tombo patrimonial e tente novamente.");
            	}
            	else if (result){
            		Stage stage = (Stage) botaoCancelar.getScene().getWindow();
    	            stage.close();
    	            alerta.notificacaoSucessoSalvarDados("Cadastrar livro");
            	}
            	mudarComportamentoTela(false);	
    		}
        };
    }
}
