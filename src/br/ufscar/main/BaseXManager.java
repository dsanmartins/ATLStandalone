package br.ufscar.main;
/*
 *   Copyright 2013 Daniel Gustavo San Martín Santibáñez

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.Export;
import org.basex.core.cmd.Open;
import org.basex.io.serial.Serializer;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.item.Item;



public class BaseXManager   {

	Context context; 
	CreateDB createDB;
	String path;
	Open openDb;
	Close closeDb;
	DropDB dropDB;
	Export export;

	/*
	 * Esse método cria um novo banco de dados. Persiste o arquivo XML.
	 */
	public BaseXManager(String path, String file, String dbName) throws BaseXException
	{
		// Create a database from a local or remote XML document or XML String
		context = new Context();
		createDB = new CreateDB(dbName, path + file);
		createDB.execute(context);
		this.path = path + file; 
		openDb = new Open(dbName);
		closeDb = new Close();
		export = new Export(this.path); 
		dropDB = new DropDB(dbName);
	}

	/*
	 * Esse método cria um novo banco de dados. Persiste o arquivo XML.
	 */
	public void createDB(String path, String file, String dbName) throws BaseXException {
		// TODO Auto-generated method stub
		context = new Context();
		createDB = new CreateDB(dbName, path + file);
		createDB.execute(context);
		openDb = new Open(dbName);
	}

	/*
	 * Esse método abre uma nova conexão com o banco de dados.
	 */
	public void openDB() throws BaseXException
	{
		openDb.execute(context);
	}

	/*
	 * Esse método encerra uma conexão com o banco de dados.
	 */
	public void closeDB() throws BaseXException
	{
		closeDb.execute(context);
	}

	/*
	 * Esse método exporta um banco de dados.
	 */
	public void exportDB() throws BaseXException
	{
		export.execute(context);
	}
 
	/*
	 * Esse método apaga um banco de dados.
	 */
	public void dropDB() throws BaseXException
	{
		dropDB.execute(context);
	}

	/*
	 * Esse método pega os métodos chamados
	 */
	public ArrayList<String> getCallsTo() throws SQLException, QueryException
	{
		ArrayList<String> arrayTo = new ArrayList<String>();
		String query = PropertyFileManager.getQuery("query-1");
		QueryProcessor proc = new QueryProcessor(query, context);
		// Execute the query
		Iter iter = proc.iter();
		Item item;

		while ((item = iter.next()) != null) 
		{
			arrayTo.add(item.toJava().toString());
		}
		proc.close();

		return arrayTo;
	}

	/*
	 * Esse método pega os métodos que chamam
	 */
	public ArrayList<String> getCallsFrom() throws SQLException, QueryException
	{
		ArrayList<String> arrayTo = new ArrayList<String>();
		String query = PropertyFileManager.getQuery("query-2");
		QueryProcessor proc = new QueryProcessor(query, context);
		// Execute the query
		Iter iter = proc.iter();
		Item item;

		while ((item = iter.next()) != null) 
		{
			arrayTo.add(item.toJava().toString());
		}
		proc.close();

		return arrayTo;
	}

	
//NOVO, OBTEM PATH COM A CONDI��O
	public String getImplementationPath(String path, String rtn) throws SQLException, QueryException
	{
		String iString = " let $prod := //kdm:Segment/";
		String fString = " return data($prod) ";
		String result = "";
		String modelE = "";
		QueryProcessor proc = null;
		String f1 = "";

		//Get path for each element of the array
		String line = path;
		String[] element = line.split("\\/");
		for (int j=0; j<element.length; j++)
		{ 
			if (j==2)
			{
				String[] modelElement = element[j].split("\\.");
				String mElement = modelElement[0].substring(1);
				int nElement = Integer.parseInt(modelElement[1]) + 1;
				modelE = mElement+"["+nElement+"]/";
			}	 

			if (j>2)
			{
				String[] codeElement = element[j].split("\\.");
				String sElement = codeElement[0].substring(1);
				int nElement = Integer.parseInt(codeElement[1]) + 1;
				String f1Element = sElement+"["+nElement+"]/@"+rtn;
				String f2Element = sElement+"["+nElement+"]/@xsi:type";
				String query = PropertyFileManager.getQuery("query-3");

				String query2 = query + iString + modelE + f1 + f2Element + fString;
				query = query + iString + modelE + f1 + f1Element + fString;
				
				proc = new QueryProcessor(query2, context);
				String type = proc.value().toJava().toString();
				proc.close();
				//Check if the element is a Package, Class or Method
				if (type.equals("code:MethodUnit") || type.equals("code:ClassUnit") || type.equals("code:Package"))
				{
					proc = new QueryProcessor(query, context);
					String name = proc.value().toJava().toString();
					proc.close();
					result = result+"."+name;
					f1Element = f1Element.substring(0, f1Element.length()-6);
					f1 = f1 + f1Element + "/";
				}
			}

		}
		return result.substring(1); 
	}
	
	
		//NOVO, PERMITE MUDAR FORMATO DO PATH A NOVO :[1] PARA SER ACEITO PELO NOVO METODO getImplementationPath
	
	public ArrayList<String> MudarFormatoPath(ArrayList<String> to) throws SQLException, QueryException
	{
		ArrayList<String> arrayList = new ArrayList<String>();	
		String result = "";
		String modelE = "";
		String f1 = "";			
		System.out.println("ultimo METODO FEITO");
		
		for (int i=0; i<to.size(); i++)
		{
			//Get path for each element of the array
			String line = to.get(i);
			String[] element = line.split("\\/");
			for (int j=0; j<element.length; j++)
			{ 
				if (j==2)
				{
					String[] modelElement = element[j].split("\\.");
					String mElement = modelElement[0].substring(1);
					int nElement = Integer.parseInt(modelElement[1]) + 1;
					modelE = mElement+"["+nElement+"]/";
				}	 

				if (j>2)
				{
					
					String[] codeElement = element[j].split("\\.");
					String sElement = codeElement[0].substring(1);
					int nElement = Integer.parseInt(codeElement[1]) + 1;
					f1 = sElement+"["+nElement+"]/";
					
					result = result+f1;
				}
				
			}
			String Final="//"+modelE+result;
			Final=Final.substring(0,Final.length()-1);// tira o ultimo caracter da cadena que � / desnecessario
			modelE="";result="";//recetea para criar a proxima linha
			arrayList.add(Final);
		}
	
		return arrayList;
	}
	
	
		//NOVO, METODO PERMITE MUDAR DE RUTA IMPLEMENT A NOMES DE PACOTES, N�O USADO.
	
	public ArrayList<String> getName(ArrayList<String> to) throws SQLException, QueryException
	{
		ArrayList<String> arrayList = new ArrayList<String>();	
		String iString = " let $prod := //kdm:Segment/";
		String fString = " return data($prod) ";
		String result = "";
		String modelE = "";
		QueryProcessor proc,procNova = null;
		String f1 = "";
	
		for (int i=0; i<to.size(); i++)
		{
			//Get path for each element of the array
			String line = to.get(i);
			String[] element = line.split("\\/");
			for (int j=0; j<element.length; j++)
			{ 
				if (j==2)
				{
					String[] modelElement = element[j].split("\\.");
					String mElement = modelElement[0].substring(1);
					int nElement = Integer.parseInt(modelElement[1]) + 1;
					modelE = mElement+"["+nElement+"]/";
				}	 

				if (j>2)
				{
					
					String[] codeElement = element[j].split("\\.");
					String sElement = codeElement[0].substring(1);
					int nElement = Integer.parseInt(codeElement[1]) + 1;
					String f1Element = sElement+"["+nElement+"]/@name";

					String query = PropertyFileManager.getQuery("query-3");
					query = query + iString + modelE + f1 + f1Element + fString;
					proc = new QueryProcessor(query, context);
					String name = proc.value().toJava().toString();
					proc.close();

					result = result+"."+name;

					f1Element = f1Element.substring(0, f1Element.length()-6);
					f1 = f1 + f1Element + "/";
					
					query = query + iString + modelE + f1 + f1Element + fString;
					
				}
			}
			arrayList.add(result.substring(1)); 
			result = "";
			f1 = "";

		}
		return arrayList;
	}
	
	

	/*
	 * Esse método obtem as informações das layers.
	 */
	public void getLayerInformation() throws SQLException, QueryException, IOException
	{
		OutputStream out = new FileOutputStream("layers.xml");
		String query = PropertyFileManager.getQuery("query-4");
		QueryProcessor proc = new QueryProcessor(query, context);
		Iter iter = proc.iter();
		Serializer ser = proc.getSerializer(out);
		for(Item item; (item = iter.next()) != null;) {
			ser.serialize(item);
		}
		out.close();
		
		String content = new String(Files.readAllBytes(Paths.get("layers.xml")));
		content = "<layers>\n" + content + "\n</layers>";
		RandomAccessFile f = new RandomAccessFile(new File("layers.xml"), "rw");
		f.seek(0);
		f.write(content.getBytes());
		f.close();
	}
	
	/* NOVO, METODO PARA MUDAR LAYER.XML
	 */

	public void SetLayerInformation(/*String nameCamada,String NovaImplementation*/) throws SQLException, QueryException
	{
		String query = PropertyFileManager.getQuery("SetImplementation");
		QueryProcessor proc = new QueryProcessor(query, context);
		
		/*proc.bind("camada", nameCamada);
		proc.bind("novaImplementation",NovaImplementation);*/
		System.out.println("se ejecuto setlayerinfo");
		//Iter iter = proc.iter();
		//Item item;
		proc.close();	
	}
	
	
	
	/*
	 * Esse método pega os métodos dado um determinado nome de classe.
	 */
	public ArrayList<String> getMethods(String className) throws SQLException, QueryException{
		
		ArrayList<String> arrMethods = new ArrayList<String>();
		String query = PropertyFileManager.getQuery("getMethodsFromClass");
		QueryProcessor proc = new QueryProcessor(query, context);
		proc.bind("class", className);
		Iter iter = proc.iter();
		Item item;

		while ((item = iter.next()) != null) 
		{
			arrMethods.add(item.toJava().toString());
		}
		proc.close();

		return arrMethods;
		
	}
	
	//METODO NUEVO
	public ArrayList<String> CriarNovaLayer() throws SQLException, QueryException{
		
		//__________//arrMethods TEM UM ARRAY DOS NOMES DAS CAMADAS, N�O USADO
		ArrayList<String> arrMethods = new ArrayList<String>();
		String query = PropertyFileManager.getQuery("GetNomesLayer");
		QueryProcessor proc = new QueryProcessor(query, context);
		//proc.bind("class", className);
		Iter iter = proc.iter();
		Item item;

		while ((item = iter.next()) != null) 
		{
			arrMethods.add(item.toJava().toString()); 
		}
		proc.close();
		//________
		
		//__________////arrMethods3 OBTEM COM UM ARRAY DAS RUTAS FROM, N�O USADO
		ArrayList<String> arrMethods3 = new ArrayList<String>();
		String query3 = PropertyFileManager.getQuery("GetFrom");
		QueryProcessor proc3 = new QueryProcessor(query3, context);
		Iter iter3 = proc3.iter();
		Item item3;
		
		// CODIGO EM USO
		
		//__________//arrMethods2 TEM UM ARRAY DAS RUTAS IMPLEMENT DAS CAMADAS
				ArrayList<String> arrMethods2 = new ArrayList<String>();
				String query2 = PropertyFileManager.getQuery("GetFieldImplementationLayer");
				QueryProcessor proc2 = new QueryProcessor(query2, context);
				Iter iter2 = proc2.iter();
				Item item2;

				while ((item2 = iter2.next()) != null) 
				{
					arrMethods2.add(item2.toJava().toString());
				}
				proc2.close();									 

		while ((item3 = iter3.next()) != null) 
		{
			arrMethods3.add(item3.toJava().toString());
		}
		proc3.close();
		
		
		ArrayList<String> arrMethods2NovoFormato=MudarFormatoPath(arrMethods2);// arrMethods2NovoFormato TEM UM ARRAY DAS RUTAS IMPLEMENT DAS CAMADAS COM NOVO FORMATO :[1]
		/*for(String line: arrMethods2NovoFormato)
			System.out.println(line);*/
		
		
		ArrayList<String> resultado=new ArrayList<String>(); 
		int tam=arrMethods.size();
		//System.out.println(tam);
		for(int i=0;i<tam;i++){
		System.out.println(arrMethods2NovoFormato.get(i)); //mostra as rutas implemeny das camadas no novo formato
		System.out.println(arrMethods.get(i)); // mostra os nomes das camadas
			resultado.add(getImplementationPath(arrMethods2NovoFormato.get(i),arrMethods.get(i)));
		}
			
		return resultado;
		
	//__________// OPCIONAL, PARA EXECUTAR OUTRAS OPERA��ES COM O METODO
	/*for(String line: arrMethods2) 
	System.out.println(line);
		ArrayList<String> arrNovo= getName(arrMethods2); //CHAMAR METODO GETNOME, MUDA DE RUTA IMPLEMENT A NOMES DE PACOTES 
		return arrNovo;
		
	}*/
	}
	
	//---------------------------------- METODOS TRABALHO
	
	//OBTEM LISTA DOS NOMES DAS CLASSES em xmiInicial.xmi
	public ArrayList<String> GetClassNames() throws SQLException, QueryException  
	{
		ArrayList<String> arrMethods = new ArrayList<String>();
		
		String query = PropertyFileManager.getQuery("GetClassNames"); //obter query
		QueryProcessor proc = new QueryProcessor(query, context);//executar query
	
		// obtem em vector
		Iter iter = proc.iter();
		Item item;
		
		while ((item = iter.next()) != null) 
		{
			arrMethods.add(item.toJava().toString());
		}
		proc.close();
		
		//System.out.println("se ejecuto GetClassNames");
		//System.out.println("");
		
		return arrMethods;
	}

	// OBTEM LISTA DOS ATRIBUTOS OU METODOS DUMA CLASSE DADA, (COM FALSE RETORNA ATRIBUTOS, COM TRUE METODOS)
	public ArrayList<String> GetClassAtrributesOrMethods(String className, boolean tipoBusca) throws SQLException, QueryException
	{
		ArrayList<String> arrMethods = new ArrayList<String>();
		
		String query = PropertyFileManager.getQuery("GetClassAtrributesOrMethods"); //obter query
		QueryProcessor proc = new QueryProcessor(query, context);//executar query
		proc.bind("class", className);
		if(tipoBusca==false)
		proc.bind("tipoBusca", "code:StorableUnit");
		else
		proc.bind("tipoBusca", "code:MethodUnit");
	
		// obtem em vector
		Iter iter = proc.iter();
		Item item;
		
		while ((item = iter.next()) != null) 
		{
			arrMethods.add(item.toJava().toString());
		}
		proc.close();
		
		//System.out.println("se ejecuto GetClassAtrributeOrMethods");
		//System.out.println("");
		
		return arrMethods;
	}

	// OBTEM UMA LISTA COM GOD CLASS(OBJETOS DE TIPO GodClass que representan classes com pelo memos 10 atributos ou metodos, valor de num)  
	public ArrayList<GodClass> GetGodClass(int num) throws SQLException, QueryException  
	{
		ArrayList<GodClass> result=new ArrayList<GodClass>();
		ArrayList<String> arrXmiClass=GetClassNames(); //obter lista com nome das classes a percorrer
		
		for(String line: arrXmiClass){ //PARA CADA CLASSE DO xmiInicial.xmi OBTEM-SE UMA LISTA DOS ATRIBUTOS E METODOS
			ArrayList<String> attributesList=GetClassAtrributesOrMethods(line, false);//COMO � FALSE, OBTEM-SE ATRIBUTOS 
			ArrayList<String> methodsList=GetClassAtrributesOrMethods(line, true);////COMO � TRUE, OBTEM-SE METODOS 
			
			if((attributesList.size()>=num || methodsList.size()>=num)){//&& !line.equals("CadastrarPessoa")
				//result.add(line);
				GodClass OgodClass=new GodClass();
				OgodClass.name=line;
				OgodClass.attributesList=attributesList;
				OgodClass.methodsList=methodsList;
			
				if(attributesList.size()==0)
					OgodClass.haveAttributes=false;
				if(methodsList.size()==0)
					OgodClass.haveMethods=false;
			
				result.add(OgodClass);
			}
			
			}
		
		return result;
	}
	
	
/*	public void GetGodClass2()throws SQLException, QueryException  {
		
	ArrayList<GodClass> arrOgodClass=GetGodClass();//otbem lista das godclass	
	
	ArrayList<String> arrAttributesList=new ArrayList<String>();
	ArrayList<String> arrMethodsList=new ArrayList<String>();
	
	int size=arrOgodClass.size();
	
	for(int i=0;i<size;i++){
		
		GodClass OgodClass=arrOgodClass.get(i);
		
		//opea��es atributos
		if(OgodClass.haveAttributes==true){ //se tem atributos
			
		}
		
		//opera��es metodos
		
		
	}
	
	
	}*/
	
	
	
	/*//VER�O VELHA DE GetGodClass
	 
	// OBTEM UMA LISTA COM OS NOMES DAS GOD CLASS(classes com pelo memos 10 atributos ou metodos)  
	public ArrayList<String> GetGodClassNames() throws SQLException, QueryException  
	{
		ArrayList<String> result=new ArrayList<String>();
		ArrayList<String> arrXmiClass=GetClassNames(); //obter lista com nome das classes a percorrer
		
		for(String line: arrXmiClass){ //PARA CADA CLASSE DO xmiInicial.xmi OBTEM-SE UMA LISTA DOS ATRIBUTOS E METODOS
			ArrayList<String> attributesList=GetClassAtrributesOrMethods(line, false);//COMO � FALSE, OBTEM-SE ATRIBUTOS 
			ArrayList<String> methodsList=GetClassAtrributesOrMethods(line, true);////COMO � TRUE, OBTEM-SE METODOS 
			
			if((attributesList.size()>=10 || methodsList.size()>=10) && !line.equals("CadastrarPessoa"))
				result.add(line);
			}
		
		return result;
	}*/
	
	/*
	public ArrayList<GodClass> GetGodClass(int attributesNum,boolean type) throws SQLException, QueryException  
	{
		//GodClass OgodClass=new GodClass();
		ArrayList<GodClass> arrGodClass=new ArrayList<GodClass>();
		ArrayList<String> arrGodClassNames=GetGodClassNames(); //obter lista com nome das godclass
		
		
		int tam=arrGodClassNames.size();
		
		for(int i=0;i<tam;i++){
			
			ArrayList<String> attributesList= GetClassAtrributesOrMethods(arrGodClassNames.get(i),false); //obtem atributos
			ArrayList<String> methodsList= GetClassAtrributesOrMethods(arrGodClassNames.get(i),true);  //obtem metodos
			GodClass OgodClass=new GodClass();
			OgodClass.name=arrGodClassNames.get(i);// atribui o nome
			OgodClass.attributeList=attributesList; // atribui a lista de atributos
			OgodClass.methodList=methodsList; // atribui a lista de metodos
			
			//System.out.println(methodsList.size());
			if(attributesList.size()==0)
				OgodClass.haveAttributes=false;
			if(methodsList.size()==0)
				OgodClass.haveMethods=false;
			
			arrGodClass.add(OgodClass);
		}
			
		return arrGodClass;
	}*/	
	
	//-------------------------------------
	
	
	
	
}