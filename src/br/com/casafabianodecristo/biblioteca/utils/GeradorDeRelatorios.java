package br.com.casafabianodecristo.biblioteca.utils;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/**
 *Classe responsável por exportar um relatório jasper em pdf
 *Ou imprimir um relatório
 *@author Matheus de Matos Oliveira
 */
/**
 * @author Matheus Matos
 *
 */
@SuppressWarnings("deprecation")
public class GeradorDeRelatorios 
{
	private String path;
	
	private String pathToReportPackage;
	
	private Connection conexao = ConnectionFactory.getConnection(); 
	
	private final String OsName = System.getProperty("os.name");
	
	/**
	 * Nome do arquivo .jrxml que deve ser compilado
	 */
	private String nomeArquivoJasper;
	
	/**
	 * Nome e caminho do arquivo pdf a ser exportado 
	 */
	private String nomeCaminhoFinalRelatorio;
	
	public GeradorDeRelatorios(String nomeArquivoJasper, String nomeCaminhoFinalRelatorio) {
		this.inicializarVariaveis();
		this.nomeArquivoJasper = nomeArquivoJasper;
		this.nomeCaminhoFinalRelatorio = nomeCaminhoFinalRelatorio;
	}
	
	public GeradorDeRelatorios(String nomeArquivoJasper) {
		this.inicializarVariaveis();
		this.nomeArquivoJasper = nomeArquivoJasper;
	}
	
	private void inicializarVariaveis(){
		this.path = this.getClass().getClassLoader().getResource("").getPath();
		System.out.println("path: " + path);
		this.pathToReportPackage = this.path + "br/com/casafabianodecristo/biblioteca/reports/";
	}
	
	
	
	/**
	 * Método que gera o relatório.
	 * @param <T>
	 * @param lista
	 * defaults to null 
	 */	
	public <T> JasperPrint gerar(List<T> lista) throws Exception	
	{
		JasperReport report = JasperCompileManager.compileReport(this.getPathToReportPackage() + nomeArquivoJasper);
		
		JasperPrint print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(lista));
		
		return print;
	}
	
	/**
	 * Método que gera o relatório em pdf a partir do SQL do relatório
	 * através de uma conexão JDBC
	 * @param Map<String, Object> mapa de parâmetros
	 * @param OutputStream saída do arquivo
	 */	
	@SuppressWarnings("rawtypes")
	public Object gerarPdf(Map<String, Object> parametros, OutputStream saida){
		String jrxml = this.getPathToReportPackage() + nomeArquivoJasper;
		//String user = System.getProperty("user.home");
		try {
			
            // compila jrxml em memoria
            JasperReport jasper = JasperCompileManager.compileReport(jrxml);
            
            //parametros.put(JRParameter.REPORT_LOCALE, Locale.);

            // preenche relatorio
            JasperPrint print = JasperFillManager.fillReport(jasper, parametros, this.conexao);

            // exporta para pdf
            JRExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, saida);
            //exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, user+"\\Documents\\Livres\\Relatórios\\TodosLivros.pdf");

            exporter.exportReport();
            
    		
    		
            //this.nomeCaminhoFinalRelatorio = (user+"\\Documents\\Livres\\Relatórios\\"+saida.toString()).toString();
            //System.out.println(nomeCaminhoFinalRelatorio);
            //JasperExportManager.exportReportToPdfStream(print, saida);
            
            return new Object();

        } catch (Exception e) {
            return null;
        }
	}
	
	/**
	 * Método que gera o relatório em pdf a partir do SQL do relatório
	 * através de uma conexão JDBC
	 * @param Map<String, Object> mapa de parâmetros
	 * @param String Nome de saída do arquivo. O caminho será padrão e montado dentro do método
	 */	
	public Object gerarPdf(Map<String, Object> parametros, String nome){
		String jrxml = this.getPathToReportPackage() + nomeArquivoJasper;
	
		try {

            // compila jrxml em memoria
            JasperReport jasper = JasperCompileManager.compileReport(jrxml);
            
            // preenche relatorio
            JasperPrint print = JasperFillManager.fillReport(jasper, parametros, this.conexao);
            String nomeComposto;
            
            if(OsName.equals("Linux"))
            	nomeComposto = System.getProperty("user.home")+"/Livres/Relatórios/"+nome;
            else 
            	nomeComposto = System.getProperty("user.home")+"\\Documents\\Livres\\Relatórios"+nome;
            
            JasperExportManager.exportReportToPdfFile(print, nomeComposto);
            
            return new Object();

        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}
	
	/**
	 * Método que gera o relatório em pdf e salva em um caminho especificado.
	 * @param <T>
	 * @param lista
	 */	
	public <T> void gerarPdf(List<T> lista) throws Exception	
	{
		JasperReport report = JasperCompileManager.compileReport(this.getPathToReportPackage() + nomeArquivoJasper);
		
		JasperPrint print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(lista));
 
		JasperExportManager.exportReportToPdfFile(print, nomeCaminhoFinalRelatorio);		
	}
	
	/**
	 * Método que recebe um relatório jasper compilado e imprime em uma determinada impresora.
	 * @param jp 
	 * @param nomeImpressora
	 */
	public void imprimir(JasperPrint jp, String nomeImpressora) throws JRException{
	    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	    printRequestAttributeSet.add(new Copies(1));
	    PrinterName printerName = new PrinterName(nomeImpressora, null); 

	    PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
	    printServiceAttributeSet.add(printerName);

	    JRPrintServiceExporter exporter = new JRPrintServiceExporter();
	    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
		exporter.exportReport();
	}
 
	public String getPathToReportPackage() {
		return this.pathToReportPackage;
	}
	
	public String getPath() {
		return this.path;
	}


	public String getNomeArquivoJasper() {
		return nomeArquivoJasper;
	}


	public void setNomeArquivoJasper(String nomeArquivoJasper) {
		this.nomeArquivoJasper = nomeArquivoJasper;
	}


	public String getNomeCaminhoFinalRelatorio() {
		return nomeCaminhoFinalRelatorio;
	}


	public void setNomeCaminhoFinalRelatorio(String nomeCaminhoFinalRelatorio) {
		this.nomeCaminhoFinalRelatorio = nomeCaminhoFinalRelatorio;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public void setPathToReportPackage(String pathToReportPackage) {
		this.pathToReportPackage = pathToReportPackage;
	}
	
	
}