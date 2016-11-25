package br.ufscar.atl;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.code.CodePackage;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;


public class ATLTransformation {
	
	public final static String IN_METAMODEL_NAME = "MM";

	//Main transformation launch method
	public void launch(String inModelPath, String outModelPath, String transformationDir, String transformationModule){
		
		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();
		Metamodel inMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		inMetamodel.setResource(CodePackage.eINSTANCE.eResource());
		env.registerMetaModel(IN_METAMODEL_NAME, inMetamodel);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new EcoreResourceFactoryImpl());
		
		// Load models
		Model inOutModel = EmftvmFactory.eINSTANCE.createModel();
		inOutModel.setResource(rs.getResource(URI.createURI(inModelPath, true), true));
		env.registerInOutModel("IN", inOutModel);
		
		ModuleResolver mr = new DefaultModuleResolver(transformationDir, rs);
		TimingData td = new TimingData();
		env.loadModule(mr, transformationModule);
		td.finishLoading();
		env.run(td);
		td.finish();
			
		// Save models
		try 
		{
			inOutModel.getResource().setURI(URI.createURI(outModelPath, true));
			inOutModel.getResource().save(Collections.emptyMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
