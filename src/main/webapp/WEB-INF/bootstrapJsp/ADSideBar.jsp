<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.recon.model.*"%>

<link href="font-awesome-4.7.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />
<style>
.headerimg{
margin-top:510px;
margin-left:60px;
position:fixed;}
.ddd {
	color: green;
	top: 433px;
	background: black;
	border-radius: 3px;
	text-align: center;
	width: 500px;
	padding: 9px;
	color: white;
}

li {
	font-size: 15px;
}
sidebar-menu{
position: relative; overflow: hidden; width: 280px; height: 1000px;
}
.iimg{
margin-bottom: 5px;
border-radius: 15px;
margin-left:10px;

}
</style>
<aside class="main-sidebar" style="width: 280px; height: 1000px;">
	<!-- sidebar: style can be found in sidebar.less -->
	<section class="sidebar"
		style="position: relative; overflow: hidden; width: 2560px; height: 1000px;">

		<ul class="sidebar-menu"
		>
			<li class="search"><span
				style="font-size: 15px; font-weight: bold;"></span></i></li>

<!-- 	<li class="treeview" style="margin-top: 20px;"> <img class="iimg"src="dist/img/pnbl_ogo.png"
			height="57px " width="260px" ></li>  -->
 			<li class="treeview"><a href="#"> <span
					style="font-size: 15px; font-weight: bold;"></span> 
<!-- 	<li class="treeview"><a href="#"> <span
					style="font-size: 15px; font-weight: bold;"></span>
			</a></li> -->
			<li class="treeview"><a href="ADMenu.do"> <i
					class="fa fa-reorder"></i> <span
					style="font-size: 15px; font-weight: bold;">DashBoard</span><small
					class="label pull-right bg-green">NEW</small>
			</a></li>

			<li class="treevi ew"><a href="#"> <i class="fa fa-group"></i>
					<span style="font-size: 15px; font-weight: bold;">Administrator</span><i
					class="fa fa-angle-left pull-right"></i>
			</a>
				<ul class="treeview-menu">
					<li><a href="UserManager.do"><i class="fa fa-user"></i> <span
							style="font-size: 15px; font-weight: bold;">User Manager</span><i
							class="fa fa-angle-left pull-right"></i></a></li>
					<li><a href="RoleManager.do"><i class="fa fa-user-plus"></i>  <span
							style="font-size: 15px; font-weight: bold;">Role Manager</span><i
							class="fa fa-angle-left pull-right"></i></a></li>
				</ul></li> 

	<!-- 		<li class="treeview"><a href="ManualUpload.do"> <i
					class="fa fa-upload"></i> <span
					style="font-size: 15px; font-weight: bold;">File Upload</span> <i
					class="fa fa-angle-left pull-right"></i>
			</a></li> -->

			<li class="treeview" style="display: none;"><a href="#"><i class="fa fa-wifi"></i>
					<span style="font-size: 15px; font-weight: bold;">Networks</span> <i
					class="fa fa-angle-left pull-right"></i> </a>
				<ul class="treeview-menu">




					<li class="treeview"><a href="#"> <i
							class="fa fa-circle-o"></i><img
							src="dist/img/cardicons/rupay.jpg" alt="rupay"> <span
							style="font-size: 15px; font-weight: bold;"> RUPAY</span><i
							class="fa fa-angle-left pull-right"></i>
					</a>
						<ul class="treeview-menu">

							<li><a href="ReconProcess.do?category=RUPAY"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Recon
										Process</span></a></li>

							<li><a href="GenerateRUPAYTTUM.do?category=RUPAY"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Generate
										TTUM</span></a></li>
							<li><a href="DownloadReports.do?category=RUPAY"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Generate
										Reports</span></a></li>
							<li><a href="rupayPresentment.do?"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Presentment
										Upload</span></a></li>
							<li><a href="rupayNetworkAdjustment.do?"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Adjustment
										Upload</span></a></li>
							<li><a href="RupayFileUpload.do?category=RUPAY"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Settlement
										Upload</span></a></li>
							<li><a href="AdjustmentTTUM.do?"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Adjustment
										Process</span></a></li>
							<li><a href="RupaySettlementProcess.do?category=RUPAY"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Settlement
										Process</span></a></li>
						</ul></li>

					<li class="treeview"><a href="#"> <i
							class="fa fa-circle-o"></i><img
							src="dist/img/cardicons/cashnet.jpg" alt="rupay"><span
							style="font-size: 15px; font-weight: bold;"> QSPARC</span><i
							class="fa fa-angle-left pull-right"></i>
					</a>
						<ul class="treeview-menu">
							<li><a href="ReconProcess.do?category=QSPARC"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
				<!-- 			<li><a href="NCMCFileUpload.do?category=QSPARC"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">File Upload</span></a></li> -->
							<li><a href="GenerateRUPAYTTUM.do?category=QSPARC"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Generate
										TTUM</span></a></li>
							<li><a href="DownloadReports.do?category=QSPARC"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Generate
										Reports</span></a></li>

							<li><a href="qsparcPresentment.do?"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Presentment
										Upload</span></a></li>
							<li><a href="QsparcNetworkAdjustment.do?"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Adjustment
										Upload</span></a></li>
							<li><a href="NCMCFileUpload.do?category=QSPARC"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Settlement
										Upload</span></a></li>
							<li><a href="QsparcAdjustmentTTUM.do?"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Adjustment
										Process</span></a></li>
							<li><a href="QsparcSettlementProcess.do?category=QSPARC"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Settlement
										Process</span></a></li>
						</ul></li>
					<li><a href="#"><i class="fa fa-circle-o"></i><img
							src="dist/img/cardicons/visa.jpg" alt="rupay"><span
							style="font-size: 15px; font-weight: bold;"> VISA</span><i
							class="fa fa-angle-left pull-right"></i></a>
						<ul class="treeview-menu">
							<li><a href="ReconProcess.do?category=VISA"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
									<li><a href="ReconProcess.do?category=VISACROSS"><i
											class="fa fa-angle-right"></i> <span
											style="font-size: 15px; font-weight: bold;">Cross
												Recon Process</span></a></li>
									<li><a href="GenerateUnmatchedTTUMVISA.do?category=VISA"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Generate
										TTUM</span></a></li>
							<li><a href="DownloadReports.do?category=VISA"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Generate
										Reports</span></a></li>





							<li><a href="VisaEPFileRead.do"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">EP File
										Upload</span></a></li>

		<!-- 					<li><a href="VisaSettlementProcessTTUM.do?category=RUPAY"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">VISA EOD
										TTUM</span></a></li>
 -->


							<li><a href="VisaSettlementProcess.do?category=VISA"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Settlement
										Process</span></a></li>
							<li><a href="VisaAdjustmentFileUpload.do"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Adjustment
										Upload</span></a></li>
							<!-- <li><a href="VISAAdjustmentTTUM.do?category=VISA"><i
									class="fa fa-angle-right"></i> <span
									style="font-size: 15px; font-weight: bold;">Salary TTUM</span></a></li>
							</a>
				 -->		<!-- 	<ul class="treeview-menu">
								<li class=""><a href="VisaMisReport.do?category=VISA"><i
										class="fa fa-upload"></i> <span
										style="font-size: 15px; font-weight: bold;">Daily
											Settlement MIS</span></a></li>
							</ul></li> -->
				</ul></li>


			<li><a href="#"><i class="fa fa-circle-o"></i><img
					src="dist/img/cardicons/nfs.jpg" alt="rupay"><span
					style="font-size: 15px; font-weight: bold;"> NFS</span><i
					class="fa fa-angle-left pull-right"></i></a>
					
					
				<ul class="treeview-menu">
			<li><a href="#"><i class="fa fa-circle-o"></i><img
					src="dist/img/cardicons/nfs.jpg" alt="rupay"><span
					style="font-size: 15px; font-weight: bold;"> NFS</span><i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">
					<li><a href="ReconProcess.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
					<li><a href="GenerateNFSTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate TTUM</span></a></li>
					<li><a href="DownloadReports.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate
								Reports</span></a></li>
					<!-- <li><a href="manulRollBack.do?category=NFS"><i
						class="fa fa-angle-right"></i> Recon RollBack</a></li></li> -->
					<li><a href="NTSLFileUpload.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NTSL Upload</span></a></li>

					<li><a href="AdjustmentFileUpload.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Upload</span></a></li>

					<li><a href="NFSAdjustmentTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Process</span></a></li>

					<li><a href="NFSSettlementTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Settlement
								Process</span></a></li>
			<!-- 		<li><a href="NIHReport.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NIH Rreports</span></a></li> --></ul></li>
		

			<li><a href="#"><i class="fa fa-circle-o"></i><img
					src="dist/img/cardicons/jcb.jfif" alt="rupay" 		height="15px " width="20px" ><span
					style="font-size: 15px; font-weight: bold;"> JCB</span><i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">
					<li><a href="ReconProcess.do?category=JCB"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
					<li><a href="GenerateNFSTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate TTUM</span></a></li>
					<li><a href="DownloadReports.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate
								Reports</span></a></li>
					<!-- <li><a href="manulRollBack.do?category=NFS"><i
						class="fa fa-angle-right"></i> Recon RollBack</a></li></li> -->
					<li><a href="JCBNTSLFileUpload.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NTSL Upload</span></a></li>

					<li><a href="JCBAdjustmentFileUpload.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Upload</span></a></li>

					<li><a href="NFSAdjustmentTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Process</span></a></li>

					<li><a href="JCBSettlementTTUM.do?category=JCB"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Settlement
								Process</span></a></li>
			<!-- 		<li><a href="NIHReport.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NIH Rreports</span></a></li> --></ul></li>
		

			<li><a href="#"><i class="fa fa-circle-o"></i><img
					src="dist/img/cardicons/dfs.png" alt="rupay" height="15px " width="20px" ><span
					style="font-size: 15px; font-weight: bold;"> DFS</span><i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">
					<li><a href="ReconProcess.do?category=DFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
					<li><a href="GenerateNFSTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate TTUM</span></a></li>
					<li><a href="DownloadReports.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate
								Reports</span></a></li>
					<!-- <li><a href="manulRollBack.do?category=NFS"><i
						class="fa fa-angle-right"></i> Recon RollBack</a></li></li> -->
					<li><a href="DFSNTSLFileUpload.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NTSL Upload</span></a></li>

					<li><a href="DFSAdjustmentFileUpload.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Upload</span></a></li>

					<li><a href="NFSAdjustmentTTUM.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Process</span></a></li>

					<li><a href="DFSSettlementTTUM.do?category=DFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Settlement
								Process</span></a></li>
			<!-- 		<li><a href="NIHReport.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NIH Rreports</span></a></li> --></ul></li>
		
</ul></li>
		


	
		<li><a href="#"><i class="fa fa-circle-o"></i><img
				src="dist/img/cardicons/mastercard.jpg" alt="rupay"><span
				style="font-size: 15px; font-weight: bold;"> MASTERCARD</span><i
				class="fa fa-angle-left pull-right"></i></a>
			<ul class="treeview-menu">
				<li><a href="ReconProcess.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
				<li><a href="DownloadReports.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Generate
							Reports</span></a></li>
				<li><a href="GenerateMASTERCARDTTUM.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Generate TTUM</span></a></li>
		<!-- 		<li><a
					href="GenerateMastercardSalaryTTUM.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Salary TTUM</span></a></li> -->

				<!-- <li><a href="manulRollBack.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> Recon RollBack</a></li></li> -->
				<li><a href="MastercardFileUpload.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Settlement
							Upload</span></a></li>
				<li><a href="MastercardT057Upload.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">T057 File
							Upload</span></a></li>


				<li><a
					href="MastercardSettlementProcess.do?category=MASTERCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Settlement Process</span></a></li>
				<li><a href="mastercardAdjustmentTTUM.do?"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Adjustment
							Process</span></a></li>

			</ul></li>
		<li><a href="#"><i class="fa fa-circle-o"></i><img
				src="dist/img/cardicons/f1.png"  width="22px" height="22px"><span
				style="font-size: 15px; font-weight: bold;"> ICCW</span> <i
				class="fa fa-angle-left pull-right"></i></a>

	
			<ul class="treeview-menu">
				<li><a href="IccwRecon.do"> <i class="fa fa-angle-right"></i>
						<span style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
			</ul>

			<ul class="treeview-menu">
				<li><a href="IccwDownloadPage.do"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Generate Reports</span></a></li>
			</ul>
					<ul class="treeview-menu">
<!-- 				<li><a href="ManualUploadIccw.do"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">File Upload</span></a></li> -->
			</ul>
			</li>
		<li><a href="#"><i class="fa fa-circle-o"></i><img
				src="dist/img/cardicons/onus.jpg"  ><span
					style="font-size: 15px; font-weight: bold;"> ICD</span><i
					class="fa fa-angle-left pull-right"></i></a>
				<ul class="treeview-menu">
					<li><a href="ReconProcess.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>
					<li><a href="GenerateICDTTUM.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate TTUM</span></a></li>
					<li><a href="DownloadReports.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Generate Reports</span></a></li>
					<!-- <li><a href="manulRollBack.do?category=NFS"><i
						class="fa fa-angle-right"></i> Recon RollBack</a></li></li> -->
					<li><a href="ICDNTSLFileUpload.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NTSL Upload</span></a></li>

					<li><a href="ICDAdjustmentFileUpload.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Upload</span></a></li>

					<li><a href="ICDAdjustmentTTUM.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Adjustment
								Process</span></a></li>

					<li><a href="ICDSettlementTTUM.do?category=ICD"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">Settlement
								Process</span></a></li>
<!-- 					<li><a href="NIHReport.do?category=NFS"><i
							class="fa fa-angle-right"></i> <span
							style="font-size: 15px; font-weight: bold;">NIH Rreports</span></a></li> --></ul></li>

		<li><a href="#"><i class="fa fa-circle-o"></i><img
				src="dist/img/cardicons/cardtocard.jpg" alt="rupay"> <span
				style="font-size: 15px; font-weight: bold;"> CARD TO CARD</span><i
				class="fa fa-angle-left pull-right"></i></a>
			<ul class="treeview-menu">
				<li><a href="ReconProcess.do?category=CARDTOCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Recon Process</span></a></li>

				<!-- 
				<li><a href="manulRollBack.do?category=CARDTOCARD"><i
						class="fa fa-angle-right"></i> Recon RollBack</a></li> -->

				<li><a href="GenerateCardtoCardTTUM.do?category=CARDTOCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Generate TTUM</span></a></li>
				<li><a href="DownloadReports.do?category=CARDTOCARD"><i
						class="fa fa-angle-right"></i> <span
						style="font-size: 15px; font-weight: bold;">Generate
							Reports</span></a></li>
			</ul></li>
		</li>


		</ul>
		


		<!-- 		<li class="treeview">
		<li><a href="GlFileUpload.do"><i class="fa fa-upload"></i><span  style="font-size: 15px; font-weight: bold;">Upload Card File</span></a></li> </a>
		</li> -->

<!-- 
		<li><a href="FullRefundTTUM.do"><i class="fa fa-retweet"></i><span
				style="font-size: 15px; font-weight: bold;">Full Refund TTUM</span></a></li>
 -->

<!-- 			<li class="treeview"><a href="viewDashboard.do"> <i
					class="fa fa-cubes"></i> <span
					style="font-size: 15px; font-weight: bold;">Map Uploaded
						Data</span> <i class="fa fa-angle-left pull-right"></i>
			</a>
				<ul class="treeview-menu">
					<li class="treeview"><a href="mapswitchdashboard.do"> <i
							class="fa fa-clone"></i> <span
							style="font-size: 15px; font-weight: bold;">Map Switch
								Data</span> <i class="fa fa-angle-left pull-right"></i>
					</a></li>
					<li class="treeview"><a href="mapcbsdashboard.do"> <i
							class="fa fa-clone"></i> <span
							style="font-size: 15px; font-weight: bold;">Map CBS Data</span> <i
							class="fa fa-angle-left pull-right"></i>
					</a></li>
				</ul></li> -->
<!-- 			<li class="treeview"><a href="viewDashboard.do"> <i
					class="fa fa-dashcube"></i> <span
					style="font-size: 15px; font-weight: bold;">View Uploaded
						Details</span> <i class="fa fa-angle-left pull-right"></i>
			</a>
				<ul class="treeview-menu">
					<li class="treeview"><a href="viewDashboard.do"> <i
							class="fa fa-eye"></i> <span
							style="font-size: 15px; font-weight: bold;">View File
								Upload</span> <i class="fa fa-angle-left pull-right"></i>
					</a></li>
					<li><a href="deleteDashboard.do"><i class="fa fa-trash-o"></i>
							<span style="font-size: 15px; font-weight: bold;">Delete
								File</span> <i class="fa fa-angle-left pull-right"></i></a></li>
					<li><a href="increaseCount.do"><i class="fa fa-line-chart"></i>
							<span style="font-size: 15px; font-weight: bold;">Increase
								File</span><i class="fa fa-angle-left pull-right"></i></a></li>
				</ul></li> -->
<!-- 				 		<li><a href="searchData.do"><i class="fa fa-retweet"></i><span
				style="font-size: 15px; font-weight: bold;">Search RRN</span></a></li>  -->
 
<li><a href="Menu.do"><i class="fa fa-arrow-circle-o-left"></i><span
				style="font-size: 15px; font-weight: bold;">Back To User Home</span></a></li> 
 
		<li><a href="Logout.do"><i class="fa fa-sign-out"></i><span
				style="font-size: 15px; font-weight: bold;">Sign-Out</span></a></li>


		</ul>

		</li>



		</ul>

				<a href="http://www.idbiintech.com/" class="logo"><img class="headerimg"src="dist/img/logo_recon.png"
			height="80px " width="150px"></a>
		
	</section>
	<!-- /.sidebar -->
</aside>



<script>
	/* document.querySelector('.searchInput').addEventListener('input',
			function() {
				debugger;

				let filter = this.value.toLowerCase();
				let items = document.querySelectorAll('ul li');

				items.forEach(function(item) {
					let text = item.textContent,
					toLowerCase();
					if (text.includes(input)) {

						item.classList.remove('hidden');

					} else {
						item.classList.add('hidden');
					}
				});
			}); */
</script>