package com.datumdroid.android.ocr.simple;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.util.Log;

//import com.datumdroid.android.ocr.simple.Test.WebServiceTask;

public class DrugDetails {
	 private static final String SERVICE_URL = "http://ml.t.proxylocal.com/RESTFullWebService/drugservice/druganalysis";

	
	public String FetchURL(String Drugname)
	{
	  DrugCompound dc = new DrugCompound();
      dc.name="Comp1";
      dc.weightage=30;

      DrugCompound dc1 = new DrugCompound();
      dc1.name="Comp2";
      dc1.weightage=5;

      DrugCompound dc2 = new DrugCompound();
      dc2.name="Comp3";
      dc2.weightage=50;

      DrugComposition drug = new DrugComposition();
      //drug.name = Drugname;
      drug.name = "Med1";
      drug.compounds.add(dc);
      drug.compounds.add(dc1);
      drug.compounds.add(dc2);

      JSONObject jsonObj = new JSONObject();
      Log.d("URL",""+ drug.compounds.size()+"");
      try {
          jsonObj.put("Drug", drug.name);
          for(int num=1;num<=drug.compounds.size();num++) {
              jsonObj.put("Compound"+num, addNewJsonObj(drug.compounds.get(num-1).name, drug.compounds.get(num-1).weightage));

          }
          /*
          jsonObj.put("Compound3", addNewJsonObj("Comp3", 50));
          jsonObj.put("Compound2", addNewJsonObj("Comp2", 5));
          jsonObj.put("Compound1", addNewJsonObj("Comp1", 30));
          jsonObj.put("Drug", "Med1");
          */
      } catch (JSONException e) {
          e.printStackTrace();
      }
      
      Log.d("JSONobjectnew",jsonObj.toString());

      String param = "";
      try {
          param = URLEncoder.encode(jsonObj.toString(), "utf-8");
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }
      
      String URL = SERVICE_URL + "?drugname="+param;//+jsonObj.toString();// + drug;  // + "/id?id=" + Patient;
      Log.d("URL",""+ URL+"");
      return URL;
   
}
	public JSONObject addNewJsonObj(String compName, int weightage) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("CompName", compName);
        obj.put("Weightage", weightage);
        return obj;
    }
}
