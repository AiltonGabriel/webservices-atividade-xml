package ailton.webservices.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XML {

	public static boolean validarXmlXsd(String xmlFile, String xsdFile) {
		File schemaFile = new File(xsdFile);
		Source xml = new StreamSource(new File(xmlFile));
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(xml);
			return true;
		} catch (SAXException| IOException e) {};

		return false;
	}

	public static boolean validarXmlDtd(String xmlFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException e) throws SAXException {}
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}
				public void fatalError(SAXParseException e) throws SAXException {
					throw e;
				}
			});
			builder.parse(new InputSource(xmlFile));
			return true;
		} catch (ParserConfigurationException | IOException | SAXException pce) {}
		return false;
	}
	
	private static int indiceElementoFilho(Element parent, Node child) {
		NodeList list = parent.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if(child.isSameNode(list.item(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public static Document consultarEnderecoXml(String cep) {
		URL url;
		try {
			url = new URL(String.format("https://viacep.com.br/ws/%s/xml", cep));
			URLConnection con = url.openConnection();
			BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line;
			StringBuilder source = new StringBuilder();
			while ((line = input.readLine()) != null)
				source.append(line);
			input.close();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			return db.parse(new InputSource(new StringReader(source.toString())));
		} catch (IOException | SAXException | ParserConfigurationException e) {
			return null;
		}
		
	}
	
	public static void manipulacao(String xmlFile) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(new File(xmlFile));

			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("produto");
			for (int temp = 0; temp < list.getLength(); temp++) {
				Node node = list.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					element.getChildNodes();
					System.out.println(node.getNodeName());
				}
			}

			// Obtendo a raiz
			Element root = doc.getDocumentElement();

			// Tag da raiz
			root.getTagName();

			// Conteúdo textual da raiz
			root.getTextContent();

			// Obtém os elementos filhos da raiz
			root.getChildNodes();

			// Busca um elemento filho específico
			Element filho = (Element) root.getElementsByTagName("livros").item(0);

			// Obtém o índice de um elemento filho
			indiceElementoFilho(root, filho);

			// Principais iteradores de um elemento

			// Obtendo os atributos de um elemento
			filho.getAttributes();

			// Contando o total de livros da biblioteca
			int total = root.getElementsByTagName("livros").getLength();
			System.out.println(total);

			// Listando o nome dos livros
			NodeList livros = doc.getElementsByTagName("livros");
			for (int temp = 0; temp < livros.getLength(); temp++) {
				Node node = livros.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					System.out.println(element.getElementsByTagName("titulo").item(0).getTextContent());
				}
			}

			// Consultando o endereço completo da biblioteca a partir do CEP.
			Element endereco = (Element)root.getElementsByTagName("endereco").item(0);
			String cep = endereco.getAttribute("cep");
			Document response = consultarEnderecoXml(cep);
			
			System.out.println("\nEndereço:\n");
			
			System.out.printf("Número: %s\n", endereco.getElementsByTagName("numero").item(0).getTextContent());
			System.out.printf("Complemento: %s\n",
					endereco.getElementsByTagName("complemento").item(0).getTextContent());
			System.out.printf("Bairro: %s\n", response.getElementsByTagName("bairro").item(0).getTextContent());
			System.out.printf("Cidade: %s\n", response.getElementsByTagName("localidade").item(0).getTextContent());
			System.out.printf("Estado: %s\n", response.getElementsByTagName("uf").item(0).getTextContent());

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.printf("Validação XSD: %b\n", validarXmlXsd("xml\\produto.xml", "xml\\produto.xsd"));
		System.out.printf("Validação DTD: %b\n", validarXmlDtd("xml\\produto.xml"));
		System.out.println("Manipulação:");
		manipulacao("xml\\biblioteca.xml");

	}

}
