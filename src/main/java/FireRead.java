
public class FireRead {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String thisline="HDRCBS19102024577";
//		String thisline="FTRCBS2110202400000000008404980000024684849000";

		if (!thisline.toUpperCase().trim().contains("HDRCBS") &&
				 !thisline.trim().contains("FTRCBS")) {
			System.out.print("if");
		}
		else
		{
			System.out.print("else");
		}
	}

}
