package br.ufscar.main;
import java.util.ArrayList;

public class GodClass {

	public String name;
	public ArrayList<String> attributesList;
	public ArrayList<String> methodsList;
	public Boolean haveAttributes;
	public Boolean haveMethods;
	
	public GodClass(){
		name="";
		attributesList=new ArrayList<String>();
		methodsList=new ArrayList<String>();	
		haveAttributes=true;
		haveMethods=true;
	}
	
	
}
