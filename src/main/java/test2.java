public class test2 {
	public static void main(String[] args) {
  
		String dirPath = "C:\\Users\\ditint12016\\Desktop\\Ankit\\IRECON\\IRECON\\src\\main\\webapp\\WEB-INF\\RUPAY PGP Tool";
  String command = "C:\\Users\\ditint12016\\Desktop\\Ankit\\IRECON\\IRECON\\src\\\\main\\\\webapp\\\\WEB-INF\\\\RUPAY PGP Tool\\\\PGPEncryption.exe";
		try {
			System.out.println("start");
			
			Process process = Runtime.getRuntime().exec("\\\\10.0.174.26\\idbi intech\\ANKIT\\NFSPGPTool\\PGPEncryption.exe");
			System.out.println("end");
			
					
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("error"+ e);
			
		}
  } }
