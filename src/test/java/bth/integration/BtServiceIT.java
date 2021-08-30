package bth.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import bth.BTHelper;
import bth.core.bt.Bt;
import bth.core.bt.BtService;
import bth.core.datasource.DatasourceException;
import bth.core.datasource.IBtSource;
import bth.core.datasource.sql.SQLManager;
import bth.core.exception.BTException;
import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.planning.PlanningService;
import bth.core.schedule.ScheduleService;

import java.text.ParseException;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class BtServiceIT {
	
	public static ArrayList<Bt> btList;
	static {
		btList = new ArrayList<Bt>();
		
		btList.add(new Bt("7888690","30/08/2021 17:01:01","T2 PTE A12 PB DE LECTEUR","8770/13","0"));
		btList.add(new Bt("7888679","30/08/2021 15:49:04","T21 PTE A14 PB IMP","8770/13","0"));
		btList.add(new Bt("7888670","30/08/2021 15:30:19","E2 BQE D3 PB DE CAB","8770/31","0"));
		btList.add(new Bt("7888668","30/08/2021 15:21:50","T22 BQE D1 PB DE CAB","8770/31","0"));
		btList.add(new Bt("7888665","30/08/2021 15:13:56","T2 BQE A03 PB DE CAB","8770/12","0"));
		btList.add(new Bt("7888662","30/08/2021 15:06:28","T21 PTE A12 IMP","8770/13","0"));
		btList.add(new Bt("7888654","30/08/2021 14:51:25","T2 PTE A13 PB DE CAB","8770/12","0"));
		btList.add(new Bt("7888653","30/08/2021 14:50:51","T2 BQE A16 PB DE PC","8770/12","0"));
		btList.add(new Bt("7888652","30/08/2021 14:48:28","T21 PTE A11 IMP","8770/13","0"));
		btList.add(new Bt("7888651","30/08/2021 14:47:21","T22 PORTE A3 PB IMP","8770/13","0"));
		btList.add(new Bt("7888648","30/08/2021 14:37:39","T21 PTE B22 PB DE PC","8770/31","0"));
		btList.add(new Bt("7888647","30/08/2021 14:36:47","T22 PORTE B27 PB DE CAB","8770/32","0"));
		btList.add(new Bt("7888634","30/08/2021 14:03:04","t21 porte a3 pb imp","8770/13","0"));
		btList.add(new Bt("7888633","30/08/2021 14:02:25","T21 bqe b12 pb de tag","8770/12","0"));
		btList.add(new Bt("7888607","30/08/2021 12:36:03","T1 BQ D35 PB CAB","8770/02","0"));
		btList.add(new Bt("7888579","30/08/2021 11:06:37","T2.1 PORTE A07 PB GATE-READER","8770/13","0"));
		btList.add(new Bt("7888574","30/08/2021 10:39:18","T2.1 BQ A10 PB TAG","8770/12","0"));
		btList.add(new Bt("7888529","30/08/2021 08:46:11","T2.2 PORTE A17 PB E-GATE","8770/13","0"));
		btList.add(new Bt("7888517","30/08/2021 08:02:47","T2.2 BQ D17 PB CAB","8770/12","0"));
		btList.add(new Bt("7888515","30/08/2021 07:53:54","T1 BQ D36 PB CAB","8770/02","0"));
		btList.add(new Bt("7888508","30/08/2021 07:33:32","T2.1 BQ A02 PB TAG","8770/12","0"));
		btList.add(new Bt("7888504","30/08/2021 07:26:55","T2.1 BQ A16 PB CAB","8770/12","0"));
		btList.add(new Bt("7878184","30/08/2021 05:31:57","T2 BQE C9 PB SWIPE","8770/12","0"));
		btList.add(new Bt("7875181","29/08/2021 15:05:44","ICA OPS AVP ROULEAU 3 PLIS","8770/04","0"));
		btList.add(new Bt("7875150","29/08/2021 12:48:48","E2 BQE D10 PB DE TAG","8770/31","0"));
		btList.add(new Bt("7875133","29/08/2021 11:43:00","T2 BQE A10 PB DE TAG","8770/12","0"));
		btList.add(new Bt("7875129","29/08/2021 11:30:52","E2 BQE D00 PB DE CAB","8770/31","0"));
		btList.add(new Bt("7875125","29/08/2021 11:19:23","T21 BQE B1 PB DE TAG","8770/12","0"));
		btList.add(new Bt("7875122","29/08/2021 11:16:50","T22 BQE D10 PB DE TAG","8770/31","0"));
		btList.add(new Bt("7875120","29/08/2021 11:15:38","T22 PORTE B27 PB DE PC","8770/32","0"));
		btList.add(new Bt("7875119","29/08/2021 11:14:06","E2 PTE B27 PB IMP","8770/32","0"));
		btList.add(new Bt("7875113","29/08/2021 10:51:56","t1 bqe c19 pb de tag","8770/02","0"));
		btList.add(new Bt("7875112","29/08/2021 10:51:08","t2 bqe b6 pb de pc","8770/12","0"));
		btList.add(new Bt("7875104","29/08/2021 10:28:58","e2 bqe c19 pb de tag","8770/31","0"));
		btList.add(new Bt("7875092","29/08/2021 09:54:50","T22 BQE D00 PB DE LP","8770/31","0"));
		btList.add(new Bt("7875086","29/08/2021 09:24:35","T21 BQE A04 PB DE DOUCHETTE","8770/12","0"));
		btList.add(new Bt("7875085","29/08/2021 09:14:33","E2 BQE D8 PB DE TAG","8770/31","0"));
		btList.add(new Bt("7875083","29/08/2021 09:06:38","T21 BQE A18 PB DE CAB","8770/12","0"));
		btList.add(new Bt("7875081","29/08/2021 08:55:56","E2 BQE D5 PB DE LP","8770/31","0"));
		btList.add(new Bt("7875076","29/08/2021 08:40:37","T22 PASSERELLE 46C PB DE PC","8770/32","0"));
		btList.add(new Bt("7875072","29/08/2021 08:28:59","T22 PASSERELLE 40A PB DE PC","8770/32","0"));
		btList.add(new Bt("7875071","29/08/2021 08:26:23","T1 BQE D36 PB DE TAG","8770/02","0"));
		btList.add(new Bt("7875067","29/08/2021 08:11:04","E2 BQE D15 PB DE SWIPE ET DE CAB","8770/31","0"));
		btList.add(new Bt("7875061","29/08/2021 07:01:16","e2 pte a18 pb aff","8770/32","0"));
		btList.add(new Bt("7875055","29/08/2021 06:46:15","e2 bqe c2 pb de tag","8770/31","0"));
		btList.add(new Bt("7875053","29/08/2021 06:42:24","e2 pte a17 pb de egate","8770/32","0"));
		btList.add(new Bt("7875045","29/08/2021 05:14:28","T2 BQE C3 PB CAB","8770/12","0"));
		btList.add(new Bt("7875031","28/08/2021 21:26:46","T21 PTE A09 PB PC EGATE","8770/13","0"));
		btList.add(new Bt("7875026","28/08/2021 20:12:40","T21 PTE A01 PB PC EGATE","8773/T2","0"));
		btList.add(new Bt("7875013","28/08/2021 19:07:42","T22 PTE B21/B22 PB SYSTREME ERES","8770/32","0"));
		btList.add(new Bt("7874998","28/08/2021 17:40:44","T21 PTE A11 IMPRIMANTE CASSEE","8770/13","0"));
		btList.add(new Bt("7874996","28/08/2021 17:18:01","T22 BQE C16 PB TAG","8770/31","0"));
		btList.add(new Bt("7874990","28/08/2021 16:48:38","T21 BQE A17 PB TAG","8770/12","0"));
		btList.add(new Bt("7874981","28/08/2021 15:29:35","T22 BQES AFR PB SYSTEME ALTEA","8770/31","0"));
		btList.add(new Bt("7874978","28/08/2021 15:12:15","T22 PTE A17 PB EGATE","8773/T2","0"));
		btList.add(new Bt("7874960","28/08/2021 14:02:52","T21 BQE A10 TAG","8770/12","0"));
		btList.add(new Bt("7874959","28/08/2021 14:01:10","T22 PORTILLON PAXTRACK GAUCHE HS","8772/02","0"));
		btList.add(new Bt("7874690","27/08/2021 16:58:10","T2 BQE B01 PC HORRAIRE BLOQUER.","8770/12","0"));
		btList.add(new Bt("7874674","27/08/2021 16:19:32","t2 poste shek bls hs.","8770/31","0"));
		btList.add(new Bt("7874667","27/08/2021 16:05:42","T22 DBA C12 HS.","8770/34","0"));
		btList.add(new Bt("7874662","27/08/2021 15:55:50","T2 PTE B24 PC BLOQUER","8770/31","0"));
		btList.add(new Bt("7874656","27/08/2021 15:47:22","T2 PC BLOQUER ENTYRE 52C ET 52A","8770/31","0"));
		btList.add(new Bt("7874639","27/08/2021 15:19:51","T2 BQE D02 TAG BLOQUER.","8770/31","0"));
		btList.add(new Bt("7874637","27/08/2021 15:15:56","T2 BQE A11 IMPRIMANTE HS.","8770/12","0"));
		btList.add(new Bt("7874636","27/08/2021 15:15:02","T1 BQE C24 PB PC.","8770/02","0"));
		btList.add(new Bt("7874635","27/08/2021 15:13:19","T2 PTE A15 PC BLOQUER","8770/31","0"));
		btList.add(new Bt("7874633","27/08/2021 15:05:20","T1 BQE 21 ET C22 CAB BLOQUER.","8770/02","0"));
		btList.add(new Bt("7874622","27/08/2021 14:47:32","T2 BQE A03 CAB BLOQUER.","8770/12","0"));
		btList.add(new Bt("7874595","27/08/2021 13:48:36","T2 PTE A1 PB SYSTEM","8770/13","0"));
		btList.add(new Bt("7874576","27/08/2021 13:15:56","e2 bqe d3 pb tag","8770/31","0"));
		btList.add(new Bt("7874561","27/08/2021 12:40:21","T2 BQE A6 PB TAG","8770/12","0"));
		btList.add(new Bt("7874560","27/08/2021 12:39:38","E2 BQE C6 PB DOUCHETTE","8770/31","0"));
		btList.add(new Bt("7874557","27/08/2021 12:29:38","E2 PB GENERAL SUR LES SERVEUR SYSTEM","8770","0"));
		btList.add(new Bt("7874548","27/08/2021 12:07:56","E2 BQE D7 PB CAB","8770/31","0"));
		btList.add(new Bt("7874544","27/08/2021 11:51:10","E2 BQE C3 PB CAB","8770/31","0"));
		btList.add(new Bt("7874315","26/08/2021 14:24:57","E2 pte a17 plus de tickets de liste attente af.","8770/32","0"));
		btList.add(new Bt("7871647","20/08/2021 11:29:37","T2 ECH BLS 2 PB SYST","8770/12","0"));
		btList.add(new Bt("7871646","20/08/2021 11:28:56","T2 BQE D0 PB CAB","8770/12","0"));
		btList.add(new Bt("7871627","20/08/2021 10:29:15","T2 PTE B24 PB SYST","8770/13","0"));
		btList.add(new Bt("7871626","20/08/2021 10:28:46","T2 BQE C17 PB AFFICH","8770/12","0"));
		btList.add(new Bt("7871625","20/08/2021 10:28:01","T2 PTE A2 PB AFFICH","8770/13","0"));
		btList.add(new Bt("7871624","20/08/2021 10:24:10","T2 ECH BLS 2 EN DEF","8770/12","0"));
		btList.add(new Bt("7871616","20/08/2021 09:54:15","T2 PTE A1 PB GATE READER","8770/13","0"));
		btList.add(new Bt("7871610","20/08/2021 09:45:54","T2 PTE A14 PB GATE READER","8770/13","0"));
		btList.add(new Bt("7871609","20/08/2021 09:44:24","T2 BQE A8 PB TAG","8770/12","0"));
		btList.add(new Bt("7871586","20/08/2021 09:12:07","T2 PTE A2 PB AFFICH","8770/13","0"));
		btList.add(new Bt("7871584","20/08/2021 09:07:20","T2 BQE B1 PB TAG","8770/12","0"));
		btList.add(new Bt("7871583","20/08/2021 09:05:52","T2 PTE A14 PB CAB","8770/13","0"));
		btList.add(new Bt("7871576","20/08/2021 08:56:40","T2 BQE C5 PB CAB","8770/12","0"));
		btList.add(new Bt("7871557","20/08/2021 08:08:57","T2 BQE C8 PB SYST","8770/12","0"));
		btList.add(new Bt("7871554","20/08/2021 08:01:45","T2 PTE A4 PB GATE READER","8770/13","0"));
		btList.add(new Bt("7871530","20/08/2021 06:07:01","T2 BQE D13 PB TAG","8770/12","0"));
		btList.add(new Bt("7871381","19/08/2021 11:10:39","T2 ECH BLS 2 PB SYST","8770/12","0"));
		btList.add(new Bt("7871376","19/08/2021 11:00:55","T2 BQE B6 PB SYST","8770/12","0"));
		btList.add(new Bt("7871373","19/08/2021 10:50:53","T2 BQE D11 PB ECRAN","8770/12","0"));
		btList.add(new Bt("7871366","19/08/2021 10:12:09","T2 BQE C17 PB TAG","8770/12","0"));
		btList.add(new Bt("7871363","19/08/2021 09:45:37","T2 PTE B25 PB EGATE","8770/13","0"));
		btList.add(new Bt("7871360","19/08/2021 09:26:05","T2 BQE A18 PB TAG","8770/12","0"));
		btList.add(new Bt("7871351","19/08/2021 08:37:06","T2 BQE D9 PB BOPASS","8770/12","0"));
		btList.add(new Bt("7871338","19/08/2021 07:21:18","T2 BQE C3 PB SYST","8770/12","0"));
		btList.add(new Bt("7871198","18/08/2021 12:23:59","T2 PTE A0 DEMANDE TEST","8770/13","0"));
		btList.add(new Bt("7871180","18/08/2021 11:27:52","T2 PTE B27 PB SYST","8770/13","0"));
		btList.add(new Bt("7871172","18/08/2021 10:56:33","T2 BQE D16 PB TAG","8770/12","0"));
		btList.add(new Bt("7871167","18/08/2021 10:40:57","T2 BQE C19 PB CAB","8770/12","0"));
		btList.add(new Bt("7871143","18/08/2021 09:13:48","T2 PTE B21 PB SYST","8770/03","0"));
		btList.add(new Bt("7871141","18/08/2021 09:00:06","T2 BQE A3 PB CAB","8770/12","0"));
		btList.add(new Bt("7871115","18/08/2021 07:27:36","T2 BQE C18 PB CAB","8770/12","0"));
		btList.add(new Bt("7871114","18/08/2021 07:26:05","T2 PTE A2 PB EGATE","8770/13","0"));
		btList.add(new Bt("7871111","18/08/2021 07:04:08","T2 PTE A14 PB AFFICHAGE","8823/07","0"));
		btList.add(new Bt("7871108","18/08/2021 06:35:25","T2 PTE A9 PB EGATE","8770/13","0"));
		btList.add(new Bt("7871106","18/08/2021 06:20:37","T2 BQE C10 PB TAG","8770/12","0"));
		btList.add(new Bt("7871104","18/08/2021 06:13:51","T2 BQE A8 PB CAB","8770/12","0"));
		btList.add(new Bt("7871103","18/08/2021 06:13:29","T2 BQE A3 PB CAB","8770/12","0"));
		btList.add(new Bt("7871097","18/08/2021 05:39:59","T2.1 BQE A09 PB CAB","8770/12","0"));
		btList.add(new Bt("7871091","18/08/2021 05:14:58","T2.2 PTE A19 PB AFFICHAGE","8770/32","0"));
		btList.add(new Bt("7869356","13/08/2021 17:02:35","T2 AFFICH APOC PCE PB SIGMA","8770","0"));
		btList.add(new Bt("7868152","09/08/2021 11:18:59","T1 AFFICH APOC PB BLOC PARKING","8770","0"));
		btList.add(new Bt("7840663","26/07/2021 08:50:49","E2 BQE C09 PB CAB","8770/31","0"));
	}
	
	@Mock
	SQLManager btSource;
	
	@Test
	public void assignTest_shouldAssignVirtualListOfBt() throws OptionException, PlanningException,
	BTException, DatasourceException, PlanningDeserializeException {
		Mockito.doReturn(btList).when(btSource).getBts(null);
		Mockito.doReturn("dd/MM/yyyy HH:mm:ss").when(btSource).getDateFormat();
		
		OptionService optionService = new OptionService(BTHelper.CONF_DIRECTORY + "/" + BTHelper.CONF_NAME);
		optionService.loadConfig();
		PlanningService planningService = new PlanningService(optionService);
		ScheduleService scheduleService = new ScheduleService(optionService);
		BtService btService = new BtService(optionService, null, btSource, planningService, scheduleService);
		
		btService.assign(null);
		
		Mockito.verify(btSource, Mockito.times(1)).getBts(null);
		Mockito.verify(btSource, Mockito.times(118)).getDateFormat();
	}

}
