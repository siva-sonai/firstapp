package com.firstapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.firstapp.model.Data;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;

public class TClass {
	public static void main(String[] args) throws Exception {
		
		//set GOOGLE_APPLICATION_CREDENTIALS="C:\Users\Admin\Downloads\FirstProject-2cbd213aea76.json"

		
		System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "C:\\Users\\Admin\\Downloads\\FirstProject-2cbd213aea76.json");
		
		/*Storage storage = StorageOptions.newBuilder()
			    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("C:\\Users\\Admin\\Downloads\\FirstProject-2cbd213aea76.json")))
			    .build()
			    .getService();*/
		
		
		String filePath= "E:\\workspace\\vision-ws\\firstapp\\input\\table.jpg";
		
		TClass t = new TClass();
		t.detectText(filePath, System.out);
		System.out.println("test");
	}

	public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
		List<AnnotateImageRequest> requests = new ArrayList<>();

		ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					out.printf("Error: %s\n", res.getError().getMessage());
					return;
				}

				// For full list of available annotations, see
				// http://g.co/cloud/vision/docs
				List<Data> list = new ArrayList<>();
				for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
					out.printf("Text: %s\n", annotation.getDescription());
					BoundingPoly poly =annotation.getBoundingPoly();
					List<Vertex> vertList = poly.getVerticesList();
					
					Data d = new Data();
					d.setText(annotation.getDescription());
					Vertex v1 = vertList.get(0);
					d.setX1(v1.getX());
					d.setY1(v1.getY());
					Vertex v2 = vertList.get(0);
					d.setX2(v2.getX());
					d.setY2(v2.getY());
					list.add(d);
					out.printf("Position : %s\n", annotation.getBoundingPoly());
				}
				sortData(list);
				
			}
		}
	}
	
	public static void sortData(List<Data> list){
		Comparator<Data> yComparator = new Comparator<Data>() {
			public int compare(Data d1, Data d2){
				return d2.getMaxY() - d1.getMaxY();
			}
		};
		Comparator<Data> xComparator = new Comparator<Data>() {
			public int compare(Data d1, Data d2){
				return d2.getMaxX() - d1.getMaxX();
			}
		};
		Collections.sort(list, xComparator);
		Collections.sort(list, yComparator);
		int fY = 0;
		Map<Integer, String> map = new LinkedHashMap<>();
		String str = "";
		int k =1;
		for(int i=0;i<list.size();i++){
			Data d = list.get(i);
			if(fY == 0){
				fY = d.getMaxY();
				str = "\""+d.getText()+"\"";
				continue;
			}else {
				int s = Math.abs(fY - d.getMaxY());
				if(s <= 5){
					str = str+",\""+d.getText()+"\"";
					fY = d.getMaxY();
				}else{
					map.put(k, str);
					k++;
					str = d.getText();
					fY = d.getMaxY();
				}
			}
		}
		System.out.println(map);
		writeToCSV(map);
	}
	
	private static void writeToCSV(Map<Integer,String> map){
		Set<Integer> keys =map.keySet();
		StringBuffer sb = new StringBuffer();
		TreeSet<Integer> set = new TreeSet<>(keys);
		Iterator<Integer> iter= set.descendingIterator();
		while(iter.hasNext()){
			Integer k =iter.next();
			String str = map.get(k);
			sb.append(str+"\n");
		}
		FileWriter fw = null;
		try{
			File f = new File("E:\\workspace\\vision-ws\\firstapp\\input\\out.csv");
			fw = new FileWriter(f);
			fw.write(sb.toString());
			fw.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(fw != null){
				try{fw.close();}catch(Exception ex){}
			}
		}
		
		
	}
	
}
