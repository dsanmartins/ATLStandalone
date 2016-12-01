package br.ufscar.main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.basex.core.BaseXException;
import org.basex.query.QueryException;

import br.ufscar.atl.ATLTransformation; 

public class Executor {

	public static void main(String[] args) throws SQLException, QueryException, IOException {

		PropertyFileManager.getQueries();
		try 
		{
			BaseXManager baseXManager = new BaseXManager("./","models/xmiInicial.xmi","kdm-layer"); 
			System.out.println("Insera a quantidade minima de atributos/metodos que deve ter uma classe para ser God Class: \n");
			Scanner dado = new Scanner(System.in);
			int num= Integer.parseInt(dado.nextLine());
			System.out.println(" ");
			// MOSTRA A LISTA DAS GOD CLASS A
			ArrayList<GodClass> arr=baseXManager.GetGodClass(num); //num define quantos atributos o metodos deve ter uma classe para se considerar god class
			System.out.println("God Class:");
			int  size=arr.size();
			for(int i=0;i<size;i++)
				System.out.println((i+1)+" "+arr.get(i).name);
			System.out.println(" ");
			System.out.println("Escolha o numero da God Class a refactorar");

			Scanner dado2=new Scanner(System.in); 
			int num2=Integer.parseInt(dado2.nextLine());
			GodClass godClass=arr.get(num2-1); //godClass � a god classe escolhida pelo usuario para refactorar, tem que restar 1 para que o numero indicado pelo usuario fique em formato do vector: [0,1,2....]
			System.out.println(" ");
			System.out.println("ATRIBUTOS: ");
			//System.out.println(" ");
			if(godClass.haveAttributes==true)
			{
				int  sizeOfAttributesList=godClass.attributesList.size();
				for(int i=0;i<sizeOfAttributesList;i++)
					System.out.println((i+1)+" "+godClass.attributesList.get(i));
				System.out.println(" ");
			}
			System.out.println("METODOS: ");
			if(godClass.haveMethods==true)
			{
				int  sizeOfMethodsList=godClass.methodsList.size();
				for(int i=0;i<sizeOfMethodsList;i++)
					System.out.println((i+1)+" "+godClass.methodsList.get(i));
				System.out.println(" ");
			}



			Path path11 = Paths.get("/home/dsanmartins/workspace/ATLStandaloneHelper/God2Refactored.atl");
			Path path12 = Paths.get("/home/dsanmartins/workspace/ATLStandaloneHelper/God2Refactored.asm");
			Path path13 = Paths.get("/home/dsanmartins/workspace/ATLStandaloneHelper/God2Refactored.emftvm");
			Path path21 = Paths.get("/home/dsanmartins/workspace/ATLStandalone/transformations/God2Refactored.atl");
			Files.copy(path11,path21,StandardCopyOption.REPLACE_EXISTING);
			changeParameter(path21);
			Path path22 = Paths.get("/home/dsanmartins/workspace/ATLStandalone/transformations/God2Refactored.asm");
			Path path23 = Paths.get("/home/dsanmartins/workspace/ATLStandalone/transformations/God2Refactored.emftvm");
			Files.copy(path12,path22,StandardCopyOption.REPLACE_EXISTING);
			Files.copy(path13,path23,StandardCopyOption.REPLACE_EXISTING);
			
			callTransformation( "./models/xmiInicial.xmi",  "./models/xmiFinal.xmi", "./transformations/", "God2Refactored" );


			//Close KDM Model
			baseXManager.closeDB();
			dado.close();
			dado2.close();
		} catch (BaseXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void callTransformation(String IN_MODEL, String OUT_MODEL, String TRANSFORMATION_DIR, String TRANSFORMATION_MODULE)
	{
		ATLTransformation l = new ATLTransformation();
		l.launch(IN_MODEL, OUT_MODEL, TRANSFORMATION_DIR, TRANSFORMATION_MODULE);
	}
	
	private static void changeParameter(Path filename) throws IOException
	{
		String search = "(?m)#className";  
		String replacement = "NewClass1";
		Charset charset = StandardCharsets.UTF_8;
		String content = new String(Files.readAllBytes(filename), charset);
		content = content.replaceAll(search, replacement);
		Files.write(filename, content.getBytes(charset));
	}
}

