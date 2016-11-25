package br.ufscar.main;
import java.io.IOException;
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
	
}
