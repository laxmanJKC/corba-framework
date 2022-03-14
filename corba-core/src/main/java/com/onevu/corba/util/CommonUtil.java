package com.onevu.corba.util;

public class CommonUtil {

	//*****************************************************************************
//	      AT&T Proprietary - Use Pursuant to Company Instructions
	//*****************************************************************************
	//  FILE:			CommonUtil.java
	//
	//  DESCRIPTION:	CommonUtil object contains a group of static utilities
	//
	//  METHODS:
	//  registerName()	-	register specified name to NameService
	//  resolveName()	-	bind object from NameService for the specified name
	//  currentTime()	-	get current Time
	//
	//  File History:
	//
	//  2001/08/14 - jcw Created for onevu2001q3
	//  2002/03/19 - zwei comment out USE_NS_IOR
	//  2002/08/23 - hwang Added function isOneInTwo.
	//  2002/12/31 - hwang Added function isOneStartsWithTwo.
	//  2002/02/01 - hwang Added function waitTimerInSecond.
	//  2003/05/16 - jcw Added function convTimeSlotFrom2TierTo1Tier
//	                   & convTimeSlotFrom1TierTo2Tier
	//  2003/06/03 - hwang Added overload functions convTimeSlotDS1
	//  2003/08/08 - acc add getTidOwnerShip()
	//  2003/09/17 - hwang Changed getPortTypeFromCardType using matchrules 
	//  2003/10/09 - hwang Added getPortTypeFromEqType, getPTPLayerFromPortType.
	//  2003/10/20 - hwang Added getEqByNP, getTPParms
	//  2003/10/28 - hwang Added getPTPLayerFromFacType, getRateFromLayerRate,
//	             - hwang Added getRateFromFacType 
	//  2004/01/26 - hwang Added function lower
	//  2004/10/20 - hwang Added function isOneContainsTwo
	//  2005/02/04 - acc Added getCTPLayerRateFromFacType
	//  2005/03/14 - acc Added simplifyClfi
//		2005/10/25 - acc convert E1 timeSlot
//		2006/01/25 - acc add matchRulesG
//		2006/02/25 - acc add matchRulesH
//		2006/04/12 - hwang Added methods for P7769
	//  2006/07/08 - Mohan added features for PA231
	//  2007/01/04 - hwang changed getRateFromLayerRate to handle P8A41
//		2007/12/01 - Deena, supported MSP2 for PD162
//		2007/12/14 - Deena, Use matchrules for MSP2
	//  2008/01/22 - Prasenjit , modify getTPParms() for support PB754 - CR7
	//  2008/01/22 - hwang changed for PD079
	//  2008/05/09 - hwang changed for PC867/PE664
	//  2008/10/06 - hwang changed for PE560
	//  2009/05/12 - hwang changed for PF878 (120256)
	//  2009/09/14 - Archana added initEqtypeNortel and checkEqtype methods to 
//	               support 179102
	//  2009/09/24 - hwang changed initEqtypeNortel for 179102
	//  2012/03/15 - jcw add initOTNPortRules, getTranslatedPortForOTN,
//		                getPortGroupFromPortForOTN, getPortRangeFromPortGroupForOTN
//	                  for OTN 232737
//		2012/04/05 - ab166g added initEqtypeOTN() to handle OTN EMS for 232737
//		2014/04/27 - Lokesh added the change for 227906
//		2014/07/14 - Lokesh commented for 227906 code
//		2015/02/23 - Suguna added the NSN change for 250222b
	//  2015/12/28 - Sanal added the initEqtypeBanditNOR, initEqtypeBanditSIE for 278916j(BANDIT) project
	// 2017/03/10  - Suguna added aidConversion method for Lumentum Bidirectional ports(291662)
	// 2017/07/13  - Suguna changed initOTNPortRules, getTranslatedPortForOTN,
//	               getPortGroupFromPortForOTN, getPortRangeFromPortGroupForOTN
//	               for OTN 297071
	//*****************************************************************************

	import org.omg.CosNaming.*;
	import org.omg.CosNaming.NamingContextPackage.*;
	import org.omg.CORBA.SystemException;
	import org.omg.CORBA.UserException;
	import org.omg.CORBA.Object;
	import java.util.*;
	import java.util.regex.*;
	import java.text.*;
	import java.io.*;
	import java.sql.*;
	import oam.TLevels;
	import EMS.terminationPoint.*;

	public class CommonUtil 
	{
		private static String _SRC_ = "CommonUtil.java";
		public static final int MAXVT = 28;
		public static final int MAXVC = 21;

		public static String[][] matchRulesC = null; 
		public static String[][] matchRulesEM = null; 
		public static String[][] matchRulesED = null; 
		public static String[][] matchRulesP = null; 
		public static String[][] matchRulesLP = null; 
		public static String[][] matchRulesLW = null; 
		public static String[][] matchRulesN = null; 
		public static String[][] matchRulesTP = null; 
		public static String[][] matchRulesR = null; 
		public static String[][] matchRulesG = null; 
		public static String[][] matchRulesH = null; 
	  public static ArrayList<String> nortelEqTypeList = null;
		public static ArrayList<?> otnEqTypeList = null;
	  public static ArrayList<String> nsnEqTypeList = null;
	  // For BANDIT
	  public static ArrayList<String> banditNOREqTypeList = null;
	  public static ArrayList<String> banditSIEEqTypeList = null;
		// OTN portRules data structures
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesP3 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesP12 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesP48 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesP5 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesP50 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesPG12 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesPG48 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesPG50 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesE12 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesE48 = null;
		public static HashMap<String, HashMap<Integer, java.lang.Object>> portRulesE50 = null;
	  public static Properties properties = null;

		public static final String ebcdicString = " .<(+|&!$*);-/,%_>?:#@'=\"abcdefghijklmnopqrstuvwxyz`ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS");
		public static java.util.Date now = null;    
		public static void registerName(org.omg.CORBA.ORB orb, String objName, Object objImpl) throws OneVuException
		{
			Object initRef;
			NamingContext objectsContext;
			NamingContext tmpContext;
			NameComponent[] name;

			try
			{
				// Find initial naming context
				objectsContext = getNSRootContext(orb);
			}
			catch (OneVuException oe)
			{
				throw oe;
			}
				
			// delim1 separates the name component
			// delim2 separates the id and kind within the name component
			String delim1 = new String(".");
			String delim2 = new String(":");
			StringTokenizer stToken = new StringTokenizer(objName, delim1);
			name = new NameComponent[1];

			// all name requires at least two name components
			// In general, name starts with OneVu
			int tokenNum = stToken.countTokens();
			if (tokenNum <= 1)
			{
				throw new OneVuException("invalid object name " + objName);
			}

			String tokenName = null;

			// create a new context and bind it relative to
			// the initial context
			for (int i = 0; i < tokenNum-1; i++)
			{
				// create a new context and bind it relative to
				// the objects context
				boolean found = false;
				tokenName = stToken.nextToken();
				int index = tokenName.indexOf(delim2);
				
				if (index < 0)
				{
					// simple object name hierarchy
					name[0] = new NameComponent(tokenName, "");
				}
				else
				{
					name[0] = new NameComponent(tokenName.substring(0, index),
						tokenName.substring(index+1));
				}

				try
				{
					initRef = objectsContext.resolve(name);
					objectsContext = NamingContextHelper.narrow(initRef);
					found = true;
				}
				catch (org.omg.CosNaming.NamingContextPackage.NotFound Ex )
				{
					// it is ok not found the context here; don't return
	    		}
				catch (org.omg.CosNaming.NamingContextPackage.CannotProceed Ex )
				{
					throw new OneVuException("CannotProceed caught for " +
							tokenName + ": "+Ex);
	    		}
				catch (org.omg.CosNaming.NamingContextPackage.InvalidName Ex )
				{
					throw new OneVuException("InvalidName caught for " +
							tokenName + ": "+Ex);
				}

				if (found)
				{
					continue;
				}

				try
				{
					tmpContext = objectsContext.bind_new_context(name);
				}
				catch (org.omg.CosNaming.NamingContextPackage.NotFound Ex )
				{
					throw new OneVuException("NotFound caught for " +
						tokenName + ": "+Ex);
				}
				catch (org.omg.CosNaming.NamingContextPackage.CannotProceed Ex )
				{
					throw new OneVuException("CannotProceed caught for " +
							tokenName + ": "+Ex);
				}
				catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound Ex )
				{
					throw new OneVuException("AlreadyBound caught for " +
							tokenName + ": "+Ex);
				}
				catch (org.omg.CosNaming.NamingContextPackage.InvalidName Ex )
				{
					throw new OneVuException("InvalidName caught for " +
							tokenName + ": "+Ex);
				}
				catch ( org.omg.CORBA.SystemException Ex )
				{
					throw new OneVuException("SystemException caught for " +
							tokenName + ": "+Ex);
				}

				objectsContext = tmpContext;
			}

			// bind name to object Impl in context objects
			tokenName = stToken.nextToken();
			int index = tokenName.indexOf(delim2);

			if (index < 0)
			{
				// simple object name hierarchy
				name[0] = new NameComponent(tokenName, "");
			}
			else
			{
				name[0] = new NameComponent(tokenName.substring(0, index),
					tokenName.substring(index+1));
			}
		
			try
			{
				objectsContext.rebind(name, objImpl);
			}
			catch (org.omg.CosNaming.NamingContextPackage.NotFound Ex )
			{
				throw new OneVuException("NotFound caught for " +
						tokenName + ": "+Ex);
			}
			catch (org.omg.CosNaming.NamingContextPackage.CannotProceed Ex )
			{
				throw new OneVuException("CannotProceed caught for " +
						tokenName + ": "+Ex);
			}
			catch (org.omg.CosNaming.NamingContextPackage.InvalidName Ex )
			{
				throw new OneVuException("InvalidName caught for " +
					tokenName + ": "+Ex);
			}
			catch ( org.omg.CORBA.SystemException Ex )
			{
				throw new OneVuException("SystemException caught for " +
					tokenName + ": "+Ex);
			}
		}

		public static org.omg.CORBA.Object resolveName(org.omg.CORBA.ORB orb, String objName) throws OneVuException
		{
			org.omg.CORBA.Object initRef;
			NamingContext initContext;
			NameComponent[] name;

			try
			{
				// Find initial naming context
				initContext = getNSRootContext(orb);
			}
			catch (OneVuException oe)
			{
				throw oe;
			}

			/*  printing naming tree
			BindingListHolder bList = new BindingListHolder();
			BindingIteratorHolder bItHd = new BindingIteratorHolder();
			initContext.list(20, bList, bItHd);
	        System.out.println("-- The length of the name path is:"+
				bList.value.length);
	        for (int l = 0; l< bList.value.length; l++)
			{
	            System.out.println("id="+bList.value[l].binding_name[0].id+
					" kind="+ bList.value[l].binding_name[0].kind);
			}
			*/
				
			String tokenName = null;

			// delim1 separates the name component
			// delim2 separates the id and kind within the name component
			String delim1 = new String(".");
			String delim2 = new String(":");
			StringTokenizer stToken = new StringTokenizer(objName, delim1);

			// all name requires at least two name components
			// In general, name starts with OneVu
			int tokenNum = stToken.countTokens();
			if (tokenNum < 1)
			{ 
				throw new OneVuException("Invalid name " + objName);
			}

			name = new NameComponent[tokenNum];

			for (int i = 0; i < tokenNum; i++)
			{
				// create a new context and bind it relative to
				// the objects context
				tokenName = stToken.nextToken();
				int index = tokenName.indexOf(delim2);
				
				if (index < 0)
				{
					// simple object name hierarchy
					name[i] = new NameComponent(tokenName, "");
				}
				else
				{
					name[i] = new NameComponent(tokenName.substring(0, index),
						tokenName.substring(index+1));
				}
			}

			/* printing names
			System.out.println("Printing name:");
			for (int i = 0; i < tokenNum; i++)
			{
				System.out.println(i + ": id=" +name[i].id + " kind=" +
					name[i].kind);
			}
			*/
			
			try
			{
				initRef = initContext.resolve(name);
			}
			catch (NotFound Ex )
			{
				throw new OneVuException("NotFound caught for " +
					tokenName + ": " + Ex);
			}
			catch (CannotProceed Ex )
			{
				throw new OneVuException("CannotProceed caught for " +
					tokenName + ": "+ Ex);
			}
			catch (InvalidName Ex )
			{
				throw new OneVuException("InvalidName caught for " +
					tokenName + ": "+ Ex);
			}
			catch ( org.omg.CORBA.SystemException Ex )
			{
				throw new OneVuException("SystemException caught for " +
					tokenName + ": "+ Ex);
			}
			return initRef;
		}

		public static NamingContext getNSRootContext(org.omg.CORBA.ORB orb) 
			throws OneVuException
		{
			Object initRef;
			NamingContext initContext = null;

			String propVal = "NO";

			// Special note: when onevuns and onevu packages are not running
			// on the same machine, the NS_IOR_FILE will not be accessible by
			// any OneVu server in the onevu package
			// propVal = PropertyHandler.getIFValue("USE_NS_IOR");
			if (propVal != null && propVal.equalsIgnoreCase("YES"))
			{
				// using NS.ior string
				String fname = PropertyHandler.getIFValue("NS_IOR_FILE");
				String rootIOR = null;
				if (fname == null)
				{
					throw new OneVuException("NS_IOR_FILE should be defined when USE_NS_IOR is set");
				}

				try
				{
					BufferedReader reader = new BufferedReader(new FileReader(fname));
					rootIOR = reader.readLine();
				}
				catch (FileNotFoundException ex)
				{
					throw new OneVuException("FileNotFoundException caught when reading file:" + fname + ". NameService ior file may exist on different machine.");
				}
				catch (IOException ex)
				{
					throw new OneVuException("IOException caught when reading file:" + fname);
				}

				try
				{
					initRef = orb.string_to_object(rootIOR);
				}
				catch ( SystemException se )
				{
					throw new OneVuException("SystemException caught when string_to_object! " + se.toString());
				}
			}
			else
			{
				try
				{
					initRef = orb.resolve_initial_references("NameService");
				}
				catch (org.omg.CORBA.ORBPackage.InvalidName invnEx)
				{
					throw new OneVuException("InvalidName caught when resolve_intial_references: " + invnEx);
				}
				catch (SystemException se)
				{
					throw new OneVuException("SystemException caught when resolve_intial_references call: " + se);
				}
			}
			try
			{
				initContext = NamingContextHelper.narrow(initRef);
			}
			catch (org.omg.CORBA.BAD_PARAM bp)
			{
				throw new OneVuException("Failed to narrow NameService root context. BAD_PARAM caught when resolve_intial_references: " + bp);
			}
			return initContext;
		}

		public static String currentTime()
		{
			java.util.Date now = new java.util.Date();
			return(now.toString());
		}

		public static boolean isOneInTwo(String one, String two)
		{
			// expecting two as "," separated String

			if (one == null || two == null)
				return false;

			int len1 = one.length();
			int len2 = two.length();

			if (len1 == 0 || len2 == 0 || len1 > len2)
				return false;

			if (!two.startsWith(","))
				two = "," + two;

			if (!two.endsWith(","))
				two += ",";

			if (two.indexOf("," + one + ",") >= 0)
				return true;

			return false;	
		}

		public static boolean isOneStartsWithTwo(String one, String two)
		{
			// expecting two as "," separated String

			if (one == null || two == null)
				return false;

			int len1 = one.length();
			int len2 = two.length();

			if (len1 == 0 || len2 == 0)
				return false;

			StringTokenizer st = new StringTokenizer(two, ",");

			while (st.hasMoreTokens())
			{
				String token = st.nextToken();

				if (one.startsWith(token))
					return true;
			}
			return false;
		}

		public static boolean isOneContainsTwo(String one, String two)
		{
			// expecting two as "," separated String

			if (one == null || two == null)
				return false;

			int len1 = one.length();
			int len2 = two.length();

			if (len1 == 0 || len2 == 0)
				return false;

			StringTokenizer st = new StringTokenizer(two, ",");

			while (st.hasMoreTokens())
			{
				String token = st.nextToken();

				if (one.indexOf(token) >= 0)
					return true;
			}
			return false;
		}

		public static void waitTimerInSecond(String timer, int defaultValue)
		{
			String funcName = "waitTimerInSecond";
			int sleeptime = defaultValue;
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering ...");
			try
			{
				sleeptime = Integer.parseInt(PropertyHandler.getValue(timer));
			}
			catch (Exception e)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Exception caught: " + e.toString() + " using default sleeptime of: " + sleeptime + " for " + timer);
			}
			try
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "will sleep " + sleeptime + " seconds");
				Thread.sleep(sleeptime*1000);
			}
			catch (InterruptedException ie)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_11, "FYI: Didn't sleep enough (" + sleeptime + " seconds) on timer " + timer);
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving ...");
		}

		public static int convTimeSlotFrom2TierTo1Tier(String timeSlotStr,
			String portType)
		{
			// input: 2-tier timeslot in either N or N-M format
			// output: 1-tier timeslot number returned based on the following rules:
			//		For portType = EC1, 2-tier = N-M, 1-tier = (N-1) * 4 + M
			//		For portType = STM, 2-tier = p-x, 1-tier = (p-1) * 28 + x
			//		For portType = others, 2-tier = N, 1-tier = N
			//
			// any error: returns -1

			String funcName = "convTimeSlotFrom2TierTo1Tier()";
			/*TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Entering timeSlotStr=" + timeSlotStr + " portType=" + portType);*/
			int timeSlotInt = -1;

			// FRS assumption: only portType=EC1 has real 2-tier timeslot
			if (portType.equalsIgnoreCase("EC1"))
			{
				int[] vtNum = new int[2];
				StringTokenizer st = new StringTokenizer(timeSlotStr, "-");
				if (st.countTokens() == 2)
				{
					for (int i=0; i<2; i++)
					{
						String tmpStr = st.nextToken();
						try
						{
							vtNum[i] = Integer.parseInt(tmpStr);
						}
						catch (NumberFormatException ex)
						{
							TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
								"NumberFormatException for vt15Group[" + i +
								"]=" + tmpStr + ", ex=" + ex);
							return -1;
						}
					}
					timeSlotInt = (vtNum[0] - 1) * 4 + vtNum[1];
				}
				else
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
						"Bad timeslot=" + timeSlotStr +
						" format for EC1, don't know how to convert");
					return -1;
				}
			}
			else if (portType.equalsIgnoreCase("STM"))
			{
				String[] comp = timeSlotStr.split("-");
				int vcNum[] = new int[2];

				for (int i=0; i<2; i++)
				{
					try {vcNum[i] = Integer.parseInt(comp[i]);}
					catch (NumberFormatException ex)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, ""+ex);
						return -1;
					}
				}

				if (vcNum[0] < 1 || (vcNum[0]-1)%3 > 0)
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Invalid p = " + vcNum[0]);
					return -1;
				}
				if (vcNum[1] < 1 || (vcNum[1] > 21 && vcNum[1] < 29) ||
					(vcNum[1] > 49 && vcNum[1] < 57) || vcNum[1] > 77)
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Invalid x = " + vcNum[1]);
					return -1;
				}

				return convTimeSlotE1(vcNum[0],vcNum[1]);
			}
			else if (portType.equalsIgnoreCase("STMIOS"))
				return getVC3TsFromE1Ts(timeSlotStr);
			else
			{
				try
				{
					timeSlotInt = Integer.parseInt(timeSlotStr);
				}
				catch (NumberFormatException ex)
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
						"NumberFormatException for timeSlot=" + timeSlotStr +
						", ex=" + ex);
					return -1;
				}
			}
			/*TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Leaving timeSlotInt=" + timeSlotInt);*/
			return timeSlotInt;
		}

		public static String convTimeSlotFrom1TierTo2Tier(int timeSlotInt,
			String portType)
		{
			// input: 1-tier timeslot in numbers
			// output: 2-tier timeslot returned in either N or N-M format,
			//		based on the following rules:
			//		For portType = EC1, 1-tier=N, 2-tier=<(N-1)/4+1>-<(N-1)%4+1>
			//		For portType = others, 2-tier = N, 1-tier = N
			//
			// any error: returns null

			String funcName = "convTimeSlotFrom1TierTo2Tier()";
			/*TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Entering timeSlotInt=" + timeSlotInt + " portType=" + portType);*/
			String timeSlotStr = null;

			// FRS assumption: only portType=EC1 has real 2-tier timeslot
			if (timeSlotInt <= 0)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
					"Bad timeslot=" + timeSlotInt +
					", don't know how to convert to 2-tier");
				return null;
			}

			if (portType.equalsIgnoreCase("EC1"))
			{
				int unit1;
				int unit2;
				unit1 = getVTGrpFromTimeSlot(timeSlotInt);
				unit2 = getVT15FromTimeSlot(timeSlotInt);
				timeSlotStr = unit1 + "-" + unit2;
			}
			else
			{
				timeSlotStr = "" + timeSlotInt;
			}
			/*TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Leaving timeSlotStr=" + timeSlotStr);*/
			return timeSlotStr;
		}

		public static int getVTGrpFromTimeSlot(int timeSlotInt)
		{
			// input: 1-tier timeslot in numbers
			// output: The first part of the converted 2-tier timeslot

			String funcName = "getVTGrpFromTimeSlot()";
	        TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Entering timeSlotInt=" + timeSlotInt);
			int vtgroup = 0;

			// FRS assumption: only portType=EC1 has real 2-tier timeslot
			if (timeSlotInt <= 0)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
					"Bad timeslot=" + timeSlotInt + ", don't know how to get VTGroup");
			}
			else
			{
				vtgroup = ((timeSlotInt - 1) / 4) + 1;
			}

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Leaving vtgroup=" + vtgroup);
			return vtgroup;
		}

		public static int getVT15FromTimeSlot(int timeSlotInt)
		{
			// input: 1-tier timeslot in numbers
			// output: The second part of the converted 2-tier timeslot

			String funcName = "getVT15FromTimeSlot()";
	        TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Entering timeSlotInt=" + timeSlotInt);
			int vt15 = 0;

			// FRS assumption: only portType=EC1 has real 2-tier timeslot
			if (timeSlotInt <= 0)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
					"Bad timeslot=" + timeSlotInt +
					", don't know how to get VT1.5");
			}
			else
			{
				vt15 = ((timeSlotInt - 1) % 4) + 1;
			}

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1,
				"Leaving vt15=" + vt15);
			return vt15;
		}

		public static int convTimeSlotDS1(int sts1TimeSlot, int vt15TimeSlot)
		{
			String funcName = "convTimeSlotDS1";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Enter with sts1TimeSlot = " + sts1TimeSlot + " vt15TimeSlot = " + vt15TimeSlot);

			int ds1TimeSlot = (sts1TimeSlot - 1) * MAXVT + vt15TimeSlot;

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leave with ds1TimeSlot = " + ds1TimeSlot);

			return ds1TimeSlot;
		}

		public static int[] convTimeSlotDS1(int ds1TimeSlot)
		{
			String funcName = "convTimeSlotDS1";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Enter with ds1TimeSlot = " + ds1TimeSlot);

			int sts1TimeSlot = (ds1TimeSlot - 1) / MAXVT + 1;
			int vt15TimeSlot = (ds1TimeSlot - 1) % MAXVT + 1;

			int[] result = new int[2];
			result[0] = sts1TimeSlot;
			result[1] = vt15TimeSlot;

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leave with result = (" + result[0] + "," + result[1] + ")");

			return result;
		}
		
		public static int convTimeSlotE1(int vc4TimeSlot, int vc12TimeSlot)
		{
			String funcName = "convTimeSlotE1";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Enter with vc4TimeSlot = " + vc4TimeSlot + " vc12TimeSlot = " + vc12TimeSlot);

			int e1TimeSlot = (vc4TimeSlot - 1) * MAXVT + vc12TimeSlot;

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leave with e1TimeSlot = " + e1TimeSlot);

			return e1TimeSlot;
		}

		public static int[] convTimeSlotE1(int e1TimeSlot)
		{
			String funcName = "convTimeSlotE1";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Enter with e1TimeSlot = " + e1TimeSlot);

			int vc4TimeSlot = (e1TimeSlot - 1) / MAXVT + 1;
			int vc12TimeSlot = (e1TimeSlot - 1) % MAXVT + 1;

			int[] result = new int[2];
			result[0] = vc4TimeSlot;
			result[1] = vc12TimeSlot;

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leave with result = (" + result[0] + "," + result[1] + ")");

			return result;
		}
		
		public static void initMatchRules() throws Exception
		{
			String funcName = "initMatchRules";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");

			String fileName = PropertyHandler.getValue("MATCHRULES");
			if (fileName == null)
			{
				throw new Exception("ERROR: MATCHRULES is not defined");
			}

			BufferedReader matchRulesFile = null;
			String data = null;
			ArrayList<String> dataList = new ArrayList<String>();
			ArrayList<String> matchRulesListC = new ArrayList<String>();
			ArrayList<String> matchRulesListEM = new ArrayList<String>();
			ArrayList<String> matchRulesListED = new ArrayList<String>();
			ArrayList<String> matchRulesListP = new ArrayList<String>();
			ArrayList<String> matchRulesListLP = new ArrayList<String>();
			ArrayList<String> matchRulesListLW = new ArrayList<String>();
			ArrayList<String> matchRulesListN = new ArrayList<String>();
			ArrayList<String> matchRulesListTP = new ArrayList<String>();
			ArrayList<String> matchRulesListR = new ArrayList<String>();
			ArrayList<String> matchRulesListG = new ArrayList<String>();
			ArrayList<String> matchRulesListH = new ArrayList<String>(); 
	    int typeCCnt = 0;
			int typeEMCnt = 0;
			int typeEDCnt = 0;
			int typePCnt = 0;
			int typeLPCnt = 0;
			int typeLWCnt = 0;
			int typeNCnt = 0;
			int typeTPCnt = 0;
			int typeRCnt = 0;
			int typeGCnt = 0;
			int typeHCnt = 0;
			int currCnt = 0;
			StringTokenizer st = null;
			StringTokenizer bar_st = null;
			int index1, index2, cnt;
			String type = "";

			try
			{
				matchRulesFile = new BufferedReader(new FileReader(fileName));
				while ((data = matchRulesFile.readLine()) != null)
				{
					if (data.startsWith("#"))
						continue;
					if (data.startsWith("C"))
						typeCCnt++;
					else if (data.startsWith("EM"))
						typeEMCnt++;
					else if (data.startsWith("ED"))
						typeEDCnt++;
					else if (data.startsWith("P"))
						typePCnt++;
					else if (data.startsWith("LP"))
						typeLPCnt++;
					else if (data.startsWith("LW"))
						typeLWCnt++;
					else if (data.startsWith("N"))
						typeNCnt++;
					else if (data.startsWith("TP"))
						typeTPCnt++;
					else if (data.startsWith("R"))
						typeRCnt++;
					else if (data.startsWith("G"))
						typeGCnt++;
					else if (data.startsWith("H"))
						typeHCnt++;
					else
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Ignore unknown type: "+data);
						continue;
					}
					dataList.add(data);
				}
				matchRulesFile.close();
			}
			catch (Exception e) { throw e; }

			matchRulesC = new String[typeCCnt][];
			matchRulesEM = new String[typeEMCnt][];
			matchRulesED = new String[typeEDCnt][];
			matchRulesP = new String[typePCnt][];
			matchRulesLP = new String[typeLPCnt][];
			matchRulesLW = new String[typeLWCnt][];
			matchRulesN = new String[typeNCnt][];
			matchRulesTP = new String[typeTPCnt][];
			matchRulesR = new String[typeRCnt][];
			matchRulesG = new String[typeGCnt][];
			matchRulesH = new String[typeHCnt][];

			for (int i=0; i<typeCCnt; i++)
			{
				cnt=1;
				data = dataList.get(i);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					cnt++;
					if (cnt == 3)
					{
						st = new StringTokenizer(bar_st.nextToken(), ",");
						while (st.hasMoreTokens())	
							matchRulesListC.add(st.nextToken());	
					}
					else
						matchRulesListC.add(bar_st.nextToken());
				}
				matchRulesC[i] = matchRulesListC.toArray(new String[0]);
				matchRulesListC.clear();
			}

			currCnt += typeCCnt;
			for (int i=0; i<typeEMCnt; i++)
			{
				cnt=1;
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					cnt++;
					if (cnt == 3)
					{
						st = new StringTokenizer(bar_st.nextToken(), ",");
						while (st.hasMoreTokens())	
							matchRulesListEM.add(st.nextToken());	
					}
					else
						matchRulesListEM.add(bar_st.nextToken());
				}
				matchRulesEM[i] = matchRulesListEM.toArray(new String[0]);
				matchRulesListEM.clear();
			}

			currCnt += typeEMCnt;
			for (int i=0; i<typeEDCnt; i++)
			{
				cnt=1;
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					cnt++;
					if (cnt == 3)
					{
						st = new StringTokenizer(bar_st.nextToken(), ",");
						while (st.hasMoreTokens())	
							matchRulesListED.add(st.nextToken());	
					}
					else
						matchRulesListED.add(bar_st.nextToken());
				}
				matchRulesED[i] = matchRulesListED.toArray(new String[0]);
				matchRulesListED.clear();
			}

			currCnt += typeEDCnt;
			for (int i=0; i<typePCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListP.add(bar_st.nextToken());
				}
				matchRulesP[i] = matchRulesListP.toArray(new String[0]);
				matchRulesListP.clear();
			}

			currCnt += typePCnt;
			for (int i=0; i<typeLPCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListLP.add(bar_st.nextToken());
				}
				matchRulesLP[i] = matchRulesListLP.toArray(new String[0]);
				matchRulesListLP.clear();
			}

			currCnt += typeLPCnt;
			for (int i=0; i<typeLWCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListLW.add(bar_st.nextToken());
				}
				matchRulesLW[i] = matchRulesListLW.toArray(new String[0]);
				matchRulesListLW.clear();
			}

			currCnt += typeLWCnt;
			for (int i=0; i<typeNCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListN.add(bar_st.nextToken());
				}
				matchRulesN[i] = matchRulesListN.toArray(new String[0]);
				matchRulesListN.clear();
			}

			currCnt += typeNCnt;
			for (int i=0; i<typeTPCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListTP.add(bar_st.nextToken());
				}
				matchRulesTP[i] = matchRulesListTP.toArray(new String[0]);
				matchRulesListTP.clear();
			}

			currCnt += typeTPCnt;
			for (int i=0; i<typeRCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListR.add(bar_st.nextToken());
				}
				matchRulesR[i] = matchRulesListR.toArray(new String[0]);
				matchRulesListR.clear();
			}

			currCnt += typeRCnt;
			for (int i=0; i<typeGCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListG.add(bar_st.nextToken());
				}
				matchRulesG[i] = matchRulesListG.toArray(new String[0]);
				matchRulesListG.clear();
			}

			currCnt += typeGCnt;
			for (int i=0; i<typeHCnt; i++)
			{
				data = dataList.get(i+currCnt);
				bar_st = new StringTokenizer(data, "|");
				type = bar_st.nextToken();
				while (bar_st.hasMoreTokens())
				{
					matchRulesListH.add(bar_st.nextToken());
				}
				matchRulesH[i] = matchRulesListH.toArray(new String[0]);
				matchRulesListH.clear();
			}

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
		}

		public static String getPortTypeFromCardType(String cardType, String layerRate)
		{
			String funcName = "getPortTypeFromCardType";

			if((cardType == null))
				return null;

			if(layerRate == null)
				layerRate = "";

			if (matchRulesC == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type C is null!");
				return null;
			}

			int len;
			cardType = cardType.toUpperCase();
			layerRate = layerRate.toLowerCase();

			for (int i=0; i<matchRulesC.length; i++)
			{
				len = matchRulesC[i].length;
				for (int j=1; j<len-1; j++)
				{
					if (cardType.indexOf(matchRulesC[i][j]) >= 0)
					{
						if (matchRulesC[i][len-1].equals("*")
							|| isOneInTwo(layerRate, matchRulesC[i][len-1]))
						{
							return matchRulesC[i][0];
						}
					}
				}
			}
			return null;
		}

		public static String getPortTypeFromEqType(String emsName, String eqType, String layerRate)
		{
			String funcName = "getPortTypeFromEqType";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering... emsName = " + emsName + " eqType="+eqType+" layerRate="+layerRate);

			if (emsName == null)
				return null;

			if((eqType == null))
				return null;

			if(layerRate == null)
				layerRate = "";

			String[][] matchRulesE = null;
			if (emsName.startsWith(SharedInfo.MSPName))
			{
				if (matchRulesEM == null)
				{
					TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type EM is null!");
					return null;
				}
				matchRulesE = matchRulesEM;
			}
			else if (emsName.startsWith(SharedInfo.DMXName))
			{
				if (matchRulesED == null)
				{
					TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type ED is null!");
					return null;
				}
				matchRulesE = matchRulesED;
			}
			else
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: unsupported emsName: " + emsName);
				return null;
			}
				
			int len;
			eqType = eqType.toUpperCase();
			layerRate = layerRate.toLowerCase();

			for (int i=0; i<matchRulesE.length; i++)
			{
				len = matchRulesE[i].length;
				for (int j=1; j<len-1; j++)
				{
					if (eqType.indexOf(matchRulesE[i][j]) >= 0)
					{
						if (matchRulesE[i][len-1].equals("*")
							|| isOneInTwo(layerRate, matchRulesE[i][len-1]))
						{
							TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
							return matchRulesE[i][0];
						}
					}
				}
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return null;
		}

		public static int getPTPLayerFromPortType(String portType, String emsName)
		{
			String funcName = "getPTPLayerFromPortType";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering...portType="+portType+" emsName="+emsName);

			if((portType == null))
				return -1;

			if(emsName == null)
				return -1;

			if (matchRulesP == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type P is null!");
				return -1;
			}

			portType = portType.toUpperCase();

			for (int i=0; i<matchRulesP.length; i++)
			{
				if (portType.equals(matchRulesP[i][0]))
				{
					if (emsName.startsWith(SharedInfo.MSPName))
					{
						return Integer.parseInt(matchRulesP[i][2]);
					}
					else if (emsName.equals(SharedInfo.DMXName))
					{
						return Integer.parseInt(matchRulesP[i][3]);
					}
					else
					{
						TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: unsupported emsName: " + emsName);
						return -1;
					}
				}	
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return -1;
		}

		public static int getPTPLayerFromFacType(String fType, String emsName)
		{
			String funcName = "getPTPLayerFromFacType";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering...fType="+fType+" emsName="+emsName);

			if((fType == null))
				return -1;

			if(emsName == null)
				return -1;

			if (matchRulesP == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type P is null!");
				return -1;
			}

			fType = fType.toUpperCase();

			for (int i=0; i<matchRulesP.length; i++)
			{
				if (isOneInTwo(fType, matchRulesP[i][1]))
				{
					if (emsName.startsWith(SharedInfo.MSPName))
					{
						return Integer.parseInt(matchRulesP[i][2]);
					}
					else if (emsName.equals(SharedInfo.DMXName))
					{
						return Integer.parseInt(matchRulesP[i][3]);
					}
					else
					{
						TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: unsupported emsName: " + emsName);
						return -1;
					}
				}	
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return -1;
		}

		public static int getCTPLayerRateFromFacType(String fType, String emsName)
		{
			String funcName = "getCTPLayerFromFacType";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Entering...fType="+fType+" emsName="+emsName);

			if((fType == null))
				return -1;

			if(emsName == null)
				return -1;

			if (matchRulesP == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, 
					"ERROR: matchRules type P is null!");
				return -1;
			}

			fType = fType.toUpperCase();

			for (int i=0; i<matchRulesP.length; i++)
			{
				if (isOneInTwo(fType, matchRulesP[i][1]))
				{
					if (emsName.startsWith(SharedInfo.MSPName))
					{
						// support MSPEMEA only at this time
						return Integer.parseInt(matchRulesP[i][4]);
					}
				}	
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return -1;
		}

		public static String getProtectionAID(String emsName, String productName)
		{
			String funcName = "getProtectionAID";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering...emsName="+emsName+" productName="+productName);

			if((emsName == null))
				return null;

			if(productName == null)
				productName = "";

			if (matchRulesLP == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type LP is null!");
				return null;
			}

			productName = productName.toUpperCase();

			for (int i=0; i<matchRulesLP.length; i++)
			{
				if (emsName.equals(matchRulesLP[i][1]))
				{
					if (matchRulesLP[i][2].equals("*")
						|| productName.startsWith(matchRulesLP[i][2])) 
					{
						return matchRulesLP[i][0];
					}
				}	
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return null;
		}

		public static String getWorkingAID(String emsName)
		{
			String funcName = "getWorkingAID";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering...emsName="+emsName);

			if((emsName == null))
				return null;

			if (matchRulesLW == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type LW is null!");
				return null;
			}

			for (int i=0; i<matchRulesLW.length; i++)
			{
				if (emsName.equals(matchRulesLW[i][1]))
						return matchRulesLW[i][0];
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return null;
		}

		public static String getEqByNP(String emsName, String pName)
		{
			String funcName = "getEqByNP";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering...emsName="+emsName+" pName="+pName);

			if((emsName == null))
				return null;

			if(pName == null)
				pName = "";

			if (matchRulesN == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type N is null!");
				return null;
			}

			pName = pName.toUpperCase();

			for (int i=0; i<matchRulesN.length; i++)
			{
				if (emsName.equals(matchRulesN[i][0]))
				{
					if (isOneStartsWithTwo(pName,matchRulesN[i][1])
						|| matchRulesN[i][1].equals("*"))
					{
						return matchRulesN[i][2];
					}
				}	
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return null;
		}

		public static Properties getTPParms(String emsName, String tpType, boolean trigger)
		{
			String funcName = "getTPParms";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering...emsName="+emsName+" tpType="+tpType+" trigger=" + trigger);
			String state = null;

			Properties tpParms = new Properties();
			
			if((emsName == null))
				return null;

			if(tpType == null)
				return null;

			if (matchRulesTP == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type TP is null!");
				return null;
			}

			tpType = tpType.toUpperCase();

			if (trigger)
				state = "ON";
			else
				state = "OFF";

			StringTokenizer st = null;
			String token = null;
			int index;

			for (int i=0; i<matchRulesTP.length; i++)
			{
				if (emsName.equals(matchRulesTP[i][0])
					&& tpType.equals(matchRulesTP[i][1])
					&& state.equals(matchRulesTP[i][2]))
				{
						String prevName = "";
						String prevValue = "";
						st = new StringTokenizer(matchRulesTP[i][3], ",");
						while (st.hasMoreTokens())
						{
							token = st.nextToken();
							index = token.indexOf(":");
							if (index!=-1)
							{
								tpParms.setProperty(token.substring(0,index), token.substring(index+1));
								prevName = token.substring(0,index);
								prevValue = token.substring(index+1);
							}
							else 
								tpParms.setProperty(prevName, prevValue+","+token);
						}	
				}	
			}

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return tpParms;
		}

		public static int getRateFromLayerRate(String layerRate)
		{
			String funcName = "getRateFromLayerRate";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering... layerRate = " + layerRate);

			if(layerRate == null || layerRate.length() == 0)
				layerRate = "STS1";

			if (layerRate.indexOf("-") > 0)
			{
				String[] layerRates = layerRate.split("-");
				layerRate = layerRates[0];
			}

			if (matchRulesR == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type R is null!");
				return 0;
			}

			layerRate = layerRate.toUpperCase();

			for (int i=0; i<matchRulesR.length; i++)
			{
				if (isOneInTwo(layerRate, matchRulesR[i][1]))
				{
					return Integer.parseInt(matchRulesR[i][0]);
				}
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return 0;
		}

		public static int getRateFromFacType(String fType)
		{
			String funcName = "getRateFromFacType";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering... fType = " + fType);

			if(fType == null || fType.length() == 0)
				fType = "STS1";

			if (matchRulesR == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "ERROR: matchRules type R is null!");
				return 0;
			}

			fType = fType.toUpperCase();

			for (int i=0; i<matchRulesR.length; i++)
			{
				if (isOneInTwo(fType, matchRulesR[i][2]))
				{
					return Integer.parseInt(matchRulesR[i][0]);
				}
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
			return 0;
		}

		public static String currTime()
		{
			java.text.SimpleDateFormat format = 
				new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

			java.util.Date today = new java.util.Date();
				
			return format.format(today);
		}

		public static String currTime2()
		{
			java.text.SimpleDateFormat format = 
				new java.text.SimpleDateFormat("yyyyMMddHHmmss");

			java.util.Date today = new java.util.Date();
				
			return format.format(today);
		}

		public static boolean lower(String one, String two)
		{
			// will return is one lower than two base on EBCDIC order

			if (one == null || two == null)
				return false;

			int len, idx1, idx2;
			boolean rc = false;

			if (one.length() <= two.length())
			{
				len = one.length();
				rc = true;
			}
			else
			{
				len = two.length();
				rc = false;
			}

			for (int i=0; i<len; i++)
			{
				idx1 = ebcdicString.indexOf(one.substring(i,i+1));	
				idx2 = ebcdicString.indexOf(two.substring(i,i+1));

				if (idx1 < idx2)
					return true;
				else if (idx1 > idx2)
					return false;			
			}
			return rc;
		}

		public static String simplifyClfi(String clfi)
		{
			String funcName = "simplifyClfi";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Entering..., clfi=" + clfi);

			String newClfi = "";
			if (clfi == null || clfi.length() == 0)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
					"Bad clfi entered"); 
				return newClfi;
			}

			String desig = clfi.substring(0, 5).trim();
			String facType = clfi.substring(5, 11).trim();
			String aClli = clfi.substring(16, 27).trim();
			String zClli = clfi.substring(27).trim();

			newClfi = desig + " " + facType + " " + aClli + " " + zClli;

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Leaving...newClfi=" + newClfi);
			return newClfi;
		}

		public static int[] convE1TimeSlot(String timeSlot)
		{
			String funcName = "convE1TimeSlot";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Entering....., timeSlot=" + timeSlot);

			// convert input format P-X or X to JKLM.
			// if input is X, then J is default to 1

			int e1TS = 0, newTS = 0;

			int[] jklm = {0,0,0,0};
			int idx = timeSlot.indexOf("-");

			try {
				if (idx >= 0)
				{
					String p = timeSlot.substring(0, idx);
					String ts = timeSlot.substring(idx+1);
					jklm[0] = (Integer.parseInt(p)-1)/3+1;
					e1TS = Integer.parseInt(ts);
				}
				else
				{
					jklm[0] = 1;
					e1TS = Integer.parseInt(timeSlot);
				}
			}
			catch (NumberFormatException ex)
			{	
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, 
					"NumberFormatException, err=" + ex);
				jklm[0] = 0;
				return jklm;
			}

			// 1-21; 29-49; 57-77
			jklm[1] = (e1TS-1)/28+1;
			newTS = (e1TS-1)%28+1;
					
			jklm[2] = (newTS-1)/3+1;
			jklm[3] = (newTS-1)%3+1;

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Leaving....., jklm=" + jklm[0] + jklm[1] + jklm[2] + jklm[3]);
			return jklm;
		}

		public static String convJKLMToE1(int timeSlot)
		{
			String funcName = "convJKLMToE1";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Entering....., timeSlot=" + timeSlot);

			// NOTE: this method is convert timeSlot jklm back to p-x
			// the j value from input has to be >= 1

			int baseNum = 0;

			// convert input jklm back to p-x format
			if (timeSlot < 1000)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
					"Input has to be 4-digit long"); 
				return "";
			}

			int jNum = 0, kNum = 0, lNum = 0, mNum = 0;	
			String jklm = timeSlot+"";
			try {
				if (jklm.length() == 5)
				{
					// jNum could be from 1 to 16
					jNum = Integer.parseInt(jklm.substring(0,2));	
					kNum = Integer.parseInt(jklm.substring(2,3));	
					lNum = Integer.parseInt(jklm.substring(3,4));	
					mNum = Integer.parseInt(jklm.substring(4));
				}
				else
				{
					jNum = Integer.parseInt(jklm.substring(0,1));	
					kNum = Integer.parseInt(jklm.substring(1,2));	
					lNum = Integer.parseInt(jklm.substring(2,3));	
					mNum = Integer.parseInt(jklm.substring(3));
				}
			}
			catch (NumberFormatException ex)
			{	
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, 
					"NumberFormatException, err=" + ex);
				return "";
			}

			baseNum = (kNum-1)*28;
	 
			int pNum = (jNum-1)*3+1;
			int xNum = ((lNum-1)*3+mNum)+baseNum;
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6,
					"Leaving...., p-x=" + (pNum+"-"+xNum));
			return (pNum + "-" + xNum);	
		}

		public static int getVC3TsFromE1Ts(String timeSlot)
		{
			String funcName = "getVC3TsFromE1Ts";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Entering....., timeSlot=" + timeSlot);

			// get VC3 level of timeSlot from E1 timeSlot format p-x
			// i.e. 1-21	returns 1, 1-29 returns 2
			//			4-29 	returns 5

			int vc3TimeSlot = 0, p = 0, ts = 0, baseNum = 0;

			int idx = timeSlot.indexOf("-");

			try {
				if (idx >= 0)
				{
					String strP = timeSlot.substring(0, idx);
					p  = Integer.parseInt(strP);
					String strTS = timeSlot.substring(idx+1);
					ts = Integer.parseInt(strTS);
				}
				else
				{
					p = 1;
					ts = Integer.parseInt(timeSlot);
				}
			}
			catch (NumberFormatException ex)
			{	
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, 
					"NumberFormatException, err=" + ex);
				return 0;
			}

			// 1-21; 29-49; 57-77
			baseNum = (ts-1)/28+1;

			vc3TimeSlot = (p-1)+baseNum;
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Leaving..., vc3TimeSlot = " + vc3TimeSlot);
			return vc3TimeSlot;
		}
		
		public static int convE1TimeSlotToInt(String timeSlot)
		{
			String funcName = "convE1TimeSlotToInt";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Entering...., timeSlot=" + timeSlot);

			int intE1TS = 0;
			try {
				int[] e1TS = convE1TimeSlot(timeSlot);
				String strE1TS = e1TS[0]+"" + e1TS[1]+"" + e1TS[2]+"" + e1TS[3]+"";
				intE1TS = Integer.parseInt(strE1TS);
			}
			catch (NumberFormatException ex)
			{	
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, 
					"NumberFormatException, err=" + ex);
				return 0;
			}
			 
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
				"Leaving...., e1TS=" + intE1TS);
			return intE1TS;
		}

		public static int[] vfaxE1ToJKLM(int timeSlot)
		{
			int[] e1TS = convTimeSlotE1(timeSlot);
			int p = ((e1TS[0]-1)/3)*3 + 1;
			int x = ((e1TS[0]-1)%3)*MAXVT + e1TS[1];
			
			int[] jklm = convE1TimeSlot(""+p+"-"+x);

			return jklm;
		}

		public static int pxToVfaxE1(String px)
		{
			String[] str = null;

			if (px.indexOf("-") < 0)
				px = "1-" + px;

			str = px.split("-");
			int p = Integer.parseInt(str[0]);
			int x = Integer.parseInt(str[1]);

			int[] e1TS = new int[2];
			
			e1TS[0] = p + ((x - 1) / MAXVT);
			e1TS[1] = x % MAXVT;

			return convTimeSlotE1(e1TS[0], e1TS[1]);
		}

		public static boolean gfpNeededCardType(String cardType)
		{
			if(cardType == null || cardType.length() == 0)
				return false;

			if (matchRulesG == null)
			{
				TLevels.log.error1(_SRC_, "gfpNeededCardType", TLevels.TR_LVL_13, 
					"ERROR: matchRules type G is null!");
				return false;
			}

			for (int i=0; i<matchRulesG.length; i++)
			{
				//matchRulesG[i][0] could be NEC, LU, MSP or IOS at this time
				if (isOneStartsWithTwo(cardType, matchRulesG[i][1]))
				{
					return true;
				}
			}
			return false;
		}

		public static void printCurrentTime()
		{
			now = new java.util.Date();
			System.out.println(formatter.format(now));
		}

		public static ArrayList<String> genCBSs(String sgf)
		{
			// generate CBS list from a Nise input SGF 
			String binarySGF = binarySGF(sgf);
			if (binarySGF == null)
				return null;

			ArrayList<String> lrList = getLayerRateFromSGF(binarySGF);
			if (lrList == null)
				return null;

			return getCBSs(lrList);
		}

		public static String getLayerRateFromCBS(String cbs)
		{
			String lr = "sts";
			char ch = cbs.charAt(1);
			if (ch == '1')
				lr += "1";
			else if (ch >= '2' && ch <= '9')
				lr += ch + "c";
			else if (ch >= 'A' && ch <= 'Z')
				lr += ((int)ch-55) + "c";
			else if (ch >= 'a' && ch <= 'z')
				lr += ((int)ch-61) + "c";

			return lr;
		}

		public static int getTimeSlotFromCBS(String cbs)
		{
			int ts = 0;
			char ch = cbs.charAt(0);
			if (ch >= '1' && ch <= '9')
				ts = Character.digit(ch, 10);
			else if (ch >= 'A' && ch <= 'Z')
				ts = (int)ch - 55;
			else if (ch >= 'a' && ch <= 'z')
				ts += (int)ch - 61;

			return ts;
		}

		protected static String char2oct(char ch)
		{
			// convert channel slot from char to octal format
			switch (ch)
			{
				case 'A':
					return "4000";
				case 'B':
					return "4444";
				case 'C':
					return "4447";
				case 'D':
					return "4474";
				case 'E':
					return "4477";
				case 'F':
					return "4744";
				case 'G':
					return "4747";
				case 'H':
					return "4774";
				case 'I':
					return "4777";
				case 'J':
					return "7444";
				case 'K':
					return "7447";
				case 'L':
					return "7474";
				case 'M':
					return "7477";
				case 'N':
					return "7744";
				case 'O':
					return "7747";
				case 'P':
					return "7774";
				case 'Q':
					return "7777";
				default:
					return null;
			}
		}

		protected static String binarySGF(String sgf)
		{
			// convert sgf to Flexible SGF format (binary form)

			String funcName = "binarySGF";

			if (sgf == null)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_13, "sgf = null");
				return null;
			}

			String oct = new String();
			String str = null;

			if (sgf.equals("F-48C"))
				oct = "4000000000000000";
			else
			{
				if (sgf.length() < 6)
				{
					TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "Invalid sgf: " + sgf);
					return null;
				}

				if (sgf.startsWith("F-")) // F-wxyz
				{
					for (int i=2; i<6; i++)
					{
						str = char2oct(sgf.charAt(i));
						if (str == null)
						{
							TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "Invalid sgf: " + sgf);
							return null;
						}
						else
							oct += str;
					}
				}
				else // flexible sgf format
				{
					Pattern p = Pattern.compile("[0-7]{16}");
					Matcher m = p.matcher(sgf);
					if (m.matches())
						oct = sgf;
					else
					{
						TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "Invalid sgf: " + sgf);
						return null;	
					}
				}
			}

			String bin = new String();
			int k = 0;
			for (int i=0; i<oct.length(); i++)
			{
				k = Integer.parseInt(""+oct.charAt(i));
				switch (k)
				{
					case 0:
						bin += "000";
						break;
					case 1:
						bin += "001";
						break;
					case 2:
						bin += "010";
						break;
					case 3:
						bin += "011";
						break;
					case 4:
						bin += "100";
						break;
					case 5:
						bin += "101";
						break;
					case 6:
						bin += "110";
						break;
					case 7:
						bin += "111";
	 				break;
				}
			}
			return bin;
		}

		protected static ArrayList<String> getLayerRateFromSGF(String sgf)
		{
			String funcName = "getLayerRateFromSGF";
			// get layerRate list from a binary format sgf
			if (sgf == null || sgf.length() < 48 || sgf.charAt(0) == '0')
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "Invalid binary format of sgf: " + sgf);
				return null;
			}

			ArrayList<String> lrList = new ArrayList<String>();
			int count = 1;
			int len = sgf.length();
			for (int i=1; i<len-1; i++)
			{
				if (sgf.charAt(i) == '1')
				{
					lrList.add(""+count);
					count = 0;
				}
				count++;
			}

			if (sgf.charAt(len-1) == '1')
			{
				lrList.add(""+count);
				lrList.add("1");
			}
			else
				lrList.add(""+(++count));
			return lrList;
		}

		protected static ArrayList<String> getCBSs(ArrayList<String> lrList)
		{
			// get CBS from LayerRate
			String funcName = "getCBSs";
			if (lrList == null)
			{
				TLevels.log.error1(_SRC_, funcName, TLevels.TR_LVL_13, "lrList = null");
				return null;
			}

			int startTS = 1;
			int bandWidth = 0;
			String cbs = new String();

			ArrayList<String> cbsList = new ArrayList<String>();

			for (int i=0; i<lrList.size(); i++)
			{
				bandWidth = Integer.parseInt(lrList.get(i));
				if (startTS < 10)
					cbs += startTS;
				else if (startTS < 36)
					cbs += (char)(startTS+55);
				else
					cbs += (char)(startTS+61);

				if (bandWidth < 10)
					cbs += bandWidth;
				else if (bandWidth < 36)
					cbs += (char)(bandWidth+55);
				else
					cbs += (char)(bandWidth+61);

				if (bandWidth == 1)
					cbs += "N";
				else
					cbs += "C";

				cbsList.add(cbs);

				cbs = "";
				startTS += bandWidth;
			}
			return cbsList;
		}

		//To convert the layer rate from ets to sts for PA231
		public static String convertETStoSTS(String layerRate)
		{
			String funcName = "convertETStoSTS";
			if (layerRate.equalsIgnoreCase("ets1"))
				return "sts1";
			else if (layerRate.equalsIgnoreCase("ets3"))
				return "sts3c";
			else if (layerRate.equalsIgnoreCase("ets12"))
				return "sts12c";
			else if(layerRate.equalsIgnoreCase("ets24"))
				return "sts24c";
			return layerRate;
		}

	   /**
	    * get Layer rate for signal rate
		* @param	signalRate  String
		* @return	int
		*/	
		public static int getLayerRateForSignalRate(String signalRate) {
			if("TEX10".equalsIgnoreCase(signalRate) ||
				"OTU2".equalsIgnoreCase(signalRate))
				return 40;
			else if("OC192".equalsIgnoreCase(signalRate) ||
				"STM64".equalsIgnoreCase(signalRate))
				return 28;
			else if("OC768".equalsIgnoreCase(signalRate))
				return 91;
			else if("ETS192".equalsIgnoreCase(signalRate))
				return 113;
			else if("OC48".equalsIgnoreCase(signalRate) ||
				"STM16".equalsIgnoreCase(signalRate))
				return 104;
			else if("ODU2".equalsIgnoreCase(signalRate))
				return 108;
			else if("OTU3".equalsIgnoreCase(signalRate))
				return 109;
			return -1;
		}

		public static int getPTPLayerFromTP(boolean isSDH, TerminationPoint_T tp)
		{
			String funcName = "getPTPLayerFromTP";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering ...");

			int ptpLayer = -1;
			if (isSDH)
			{
				for (int i=0; i<tp.transmissionParams.length; i++)
				{
					ptpLayer = tp.transmissionParams[i].layer;
					if (ptpLayer == 77)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving with ptpLayer = " + ptpLayer);
						return ptpLayer;
					}
				}
			}

			ptpLayer = -1;
			for (int i=0; i<tp.transmissionParams.length; i++)
			{
				ptpLayer = tp.transmissionParams[i].layer;
				if (ptpLayer >= 20 && ptpLayer <=23)
					break;
			}

			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving with ptpLayer = " + ptpLayer);

			return ptpLayer;
		}

		public static void initEqtypeNortel() throws Exception
		{
			String funcName = "initEqtypeNortel";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");

			String fileName = PropertyHandler.getValue("EQTYPE.NORTEL");
			if (fileName == null)
			{
				throw new Exception("ERROR: EQTYPE.NORTEL is not defined");
			}

			nortelEqTypeList = new ArrayList<String>();	
			BufferedReader eqtypeNortelFile = null;
			String data = null;
		
			try
			{
				eqtypeNortelFile = new BufferedReader(new FileReader(fileName));
				while ((data = eqtypeNortelFile.readLine()) != null)
				{
					if (data.startsWith("#"))
						continue;
					nortelEqTypeList.add(data);
				}
				eqtypeNortelFile.close();
			}
			catch (Exception e) { throw e; }
		}
		public static void initEqtypeNSN() throws Exception
		{
			String funcName = "initEqtypeNSN";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");

			String fileName = PropertyHandler.getValue("EQTYPE.NSN");
			if (fileName == null)
			{
				throw new Exception("ERROR: EQTYPE.NSN is not defined");
			}

			nsnEqTypeList = new ArrayList<String>();	
			BufferedReader eqtypeNSNFile = null;
			String data = null;
		
			try
			{
				eqtypeNSNFile = new BufferedReader(new FileReader(fileName));
				while ((data = eqtypeNSNFile.readLine()) != null)
				{
					if (data.startsWith("#"))
						continue;
					nsnEqTypeList.add(data);
				}
				eqtypeNSNFile.close();
			}
			catch (Exception e) { throw e; }
		}
		//BANDIT 278916j
		public static void initEqtypeBanditNOR() throws Exception
		{
			String funcName = "initEqtypeBanditNOR";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");

			String fileName = PropertyHandler.getValue("EQTYPE_BANDIT.NOR");
			if (fileName == null)
			{
				throw new Exception("ERROR: EQTYPE_BANDIT.NOR is not defined");
			}

			banditNOREqTypeList = new ArrayList<String>();	
			BufferedReader eqtypeBanditNORFile = null;
			String data = null;
		
			try
			{
				eqtypeBanditNORFile = new BufferedReader(new FileReader(fileName));
				while ((data = eqtypeBanditNORFile.readLine()) != null)
				{
					if (data.startsWith("#"))
						continue;
					banditNOREqTypeList.add(data);
				}
				eqtypeBanditNORFile.close();
			}
			catch (Exception e) { throw e; }
		}
		public static void initEqtypeBanditSIE() throws Exception
		{
			String funcName = "initEqtypeBanditSIE";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");

			String fileName = PropertyHandler.getValue("EQTYPE_BANDIT.SIE");
			if (fileName == null)
			{
				throw new Exception("ERROR: EQTYPE_BANDIT.SIE is not defined");
			}

			banditSIEEqTypeList = new ArrayList<String>();	
			BufferedReader eqtypeBanditSIEFile = null;
			String data = null;
		
			try
			{
				eqtypeBanditSIEFile = new BufferedReader(new FileReader(fileName));
				while ((data = eqtypeBanditSIEFile.readLine()) != null)
				{
					if (data.startsWith("#"))
						continue;
					banditSIEEqTypeList.add(data);
				}
				eqtypeBanditSIEFile.close();
			}
			catch (Exception e) { throw e; }
		}
		
		public static void initOTNPortRules() throws Exception
		{
			String funcName = "initOTNPortRules";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");

			String fileName = PropertyHandler.getValue("OTNPORTRULES");
			if (fileName == null)
			{
				throw new Exception("ERROR: OTNPORTRULES is not defined");
			}

			BufferedReader portRulesFile = null;
			String data = null;
			ArrayList<String> dataListForP = new ArrayList<String>();
			ArrayList<String> dataListForPG = new ArrayList<String>();
			ArrayList<String> dataListForE = new ArrayList<String>();
			HashMap<String, HashMap<Integer, java.lang.Object>> targetMap = null;
			String cardType = null;
			String key = null;
			String value = null;
			String[] dataComp = null;
	    portRulesP3 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesP12 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesP48 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesP5 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesP50 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesPG12 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesPG48 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesPG50 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesE12 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesE48 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			portRulesE50 = new HashMap<String, HashMap<Integer, java.lang.Object>>();
			try
			{
				portRulesFile = new BufferedReader(new FileReader(fileName));
				while ((data = portRulesFile.readLine()) != null)
				{
					if (data.startsWith("#"))
						continue;
					else if (data.startsWith("PG"))
					{
						dataListForPG.add(data);
					}
					else if (data.startsWith("P"))
					{
						dataListForP.add(data);
					}
					else if (data.startsWith("E"))
					{
						dataListForE.add(data);
					}
					else
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Ignore unknown type: "+data);
						continue;
					}
				}
				portRulesFile.close();
			}
			catch (Exception e) { throw e; }

			HashMap<Integer, java.lang.Object> myMap1;
			boolean isNew;

			// Parse Port Translation data line
			for (int i=0; i<dataListForP.size(); i++)
			{
				data = dataListForP.get(i);
				dataComp = data.split("\\|");
				//TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "dataComp Length:" +dataComp.length);
				if (dataComp.length != 4)
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
				}

				cardType = dataComp[1];
				//TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "cardType is:" +cardType);
				key = dataComp[2];
				value = dataComp[3];

				if ("OSLM3".equalsIgnoreCase(cardType)  )
					targetMap = portRulesP3;
				else if ("OSLM12".equalsIgnoreCase(cardType))
					targetMap = portRulesP12;
				else if ("OSLM48".equalsIgnoreCase(cardType))
					targetMap = portRulesP48;
				else if ("OSLM5".equalsIgnoreCase(cardType))
					targetMap = portRulesP5;
				else if ("OSLM50".equalsIgnoreCase(cardType))
					targetMap = portRulesP50;
				else
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
					continue;
				}

				isNew = false;

				if (targetMap.containsKey(key))
					myMap1 =  targetMap.get(key);
				else
				{
					isNew = true;
				myMap1 = new HashMap<Integer, java.lang.Object>();
				}

				StringTokenizer st = new StringTokenizer(value, ",");
				while (st.hasMoreTokens())	
				{
					String[] comp = st.nextToken().split("=");
					if (comp.length != 2)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
					}
					try
					{
						myMap1.put(new Integer(Integer.parseInt(comp[0])), new Integer(Integer.parseInt(comp[1])));
					}
					catch (NumberFormatException ex)
	        		{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "NumberFormatException for " + fileName + " contains bad format data line (skipping): " + data);
						continue;
	        		}
				}
				if (isNew)
					targetMap.put(key, myMap1);
			}

			// Processing Port Group Translation data lines
			for (int i=0; i<dataListForPG.size(); i++)
			{
				data = dataListForPG.get(i);
				dataComp = data.split("\\|");
				if (dataComp.length != 4)
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
				}
				cardType = dataComp[1];
				key = dataComp[2];
				value = dataComp[3];

				if ("OSLM12".equalsIgnoreCase(cardType))
					targetMap = portRulesPG12;
				else if ("OSLM48".equalsIgnoreCase(cardType))
					targetMap = portRulesPG48;
				else if ("OSLM50".equalsIgnoreCase(cardType))
					targetMap = portRulesPG50;
				else
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
					continue;
				}

				isNew = false;
				if (targetMap.containsKey(key))
					myMap1 =  targetMap.get(key);
				else
				{
					isNew = true;
				myMap1 = new HashMap<Integer, java.lang.Object>();
				}
				
				StringTokenizer st = new StringTokenizer(value, ",");
				while (st.hasMoreTokens())	
				{
					String [] comp = st.nextToken().split("=");
					if (comp.length != 2)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
					}
					String[] range = comp[1].split("-");
					if (range.length != 2)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
					}
					Integer[] myRange = new Integer[2];
					try
					{
						myRange[0] = new Integer(Integer.parseInt(range[0]));
						myRange[1] = new Integer(Integer.parseInt(range[1]));
					}
					catch (NumberFormatException ex)
	        		{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "NumberFormatException for " + fileName + " contains bad format data line (skipping): " + data);
						continue;
	        		}
					myMap1.put(new Integer(Integer.parseInt(comp[0])), myRange);
				}
				if (isNew)
					targetMap.put(key, myMap1);
			}

			// Processing Type:E Port - to - Port Group Translation data lines
			for (int i=0; i<dataListForE.size(); i++)
			{
				data = dataListForE.get(i);
				dataComp = data.split("\\|");
				if (dataComp.length != 4)
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
				}
				cardType = dataComp[1];
				key = dataComp[2];
				value = dataComp[3];

				if ("OSLM12".equalsIgnoreCase(cardType))
					targetMap = portRulesE12;
				else if ("OSLM48".equalsIgnoreCase(cardType))
					targetMap = portRulesE48;
				else if ("OSLM50".equalsIgnoreCase(cardType))
					targetMap = portRulesE50;
				
				
				else
				{
					TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
					continue;
				}

				isNew = false;
				if (targetMap.containsKey(key))
					myMap1 = targetMap.get(key);
				else
				{
					isNew = true;
				myMap1 = new HashMap<Integer, java.lang.Object>();
				}
				
				StringTokenizer st = new StringTokenizer(value, ",");
				while (st.hasMoreTokens())	
				{
					String [] comp = st.nextToken().split("=");
					if (comp.length != 2)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, fileName + " contains bad format data line (skipping): " + data);
						continue;
					}
					try
					{
						myMap1.put(new Integer(Integer.parseInt(comp[0])), new Integer(Integer.parseInt(comp[1])));
					}
					catch (NumberFormatException ex)
	        		{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "NumberFormatException for " + fileName + " contains bad format data line (skipping): " + data);
						continue;
	        		}
				}
				if (isNew)
					targetMap.put(key, myMap1);
			}
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving....");
		}

		public static int getTranslatedPortForOTN(String cardType, String shelf, int port)
		{
			String funcName = "getTranslatedPortForOTN";
			HashMap<String, HashMap<Integer, java.lang.Object>> targetMap = null;
			int translatedPort = -1;
			boolean found = false;
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Entering .. OTN card=" + cardType + " shelf=" + shelf + " port=" + port);

			if ("OSLM3".equalsIgnoreCase(cardType))
				targetMap = portRulesP3;
			else if ("OSLM12".equalsIgnoreCase(cardType))
				targetMap = portRulesP12;
			else if ("OSLM48".equalsIgnoreCase(cardType))
				targetMap = portRulesP48;
			else if ("OSLM5".equalsIgnoreCase(cardType))
				targetMap = portRulesP5;
			else if ("OSLM50".equalsIgnoreCase(cardType))
				targetMap = portRulesP50;

			if (targetMap == null || shelf == null)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "bad input or OTN portRules not initialized");
				return -1;
			}

			if (targetMap.containsKey(shelf))
			{
			HashMap<Integer, java.lang.Object> portMap = targetMap.get(shelf);	
			Integer inPort = new Integer(port);
				if (portMap.containsKey(inPort))
				{
					Integer outPort = (Integer) portMap.get(inPort);
					translatedPort = outPort.intValue();
					found = true;
				}
			}

			if (found)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Leaving .. OTN card=" + cardType + " shelf=" + shelf + " translated-port=" + translatedPort);
			}
			else
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "No port translation found for card=" + cardType + " shelf=" + shelf  + " port=" + port);
			}
			return translatedPort;
		}

		public static int getPortGroupFromPortForOTN(String cardType, String shelf, int port)
		{
			String funcName = "getPortGroupFromPortForOTN";
			HashMap<String, HashMap<Integer, java.lang.Object>> targetMap = null;
			int portGroup = -1;
			boolean found = false;
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Entering .. OTN card=" + cardType + " shelf=" + shelf + " port=" + port);

			if ("OSLM12".equalsIgnoreCase(cardType))
				targetMap = portRulesE12;
			else if ("OSLM48".equalsIgnoreCase(cardType))
				targetMap = portRulesE48;
			else if ("OSLM50".equalsIgnoreCase(cardType))
				targetMap = portRulesE50;

			if (targetMap == null || shelf == null)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "bad input or OTN portRules not initialized");
				return -1;
			}

			if (targetMap.containsKey(shelf))
			{
				HashMap<Integer, java.lang.Object> portMap = targetMap.get(shelf);
				Integer inPort = new Integer(port);
				if (portMap.containsKey(inPort))
				{
					Integer myPortGroup = (Integer) portMap.get(inPort);
					portGroup = myPortGroup.intValue();
					found = true;
				}
			}

			if (found)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Leaving .. OTN card=" + cardType + " shelf=" + shelf + " portGroup=" + portGroup);
			}
			else
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "No port grouptranslation found for card=" + cardType + " shelf=" + shelf  + " port=" + port);
			}
			return portGroup;
		}

		public static Integer[] getPortRangeFromPortGroupForOTN(String cardType, String shelf, int portGroup)
		{
			String funcName = "getPortRangeFromPortGroupForOTN";
			HashMap<String, HashMap<Integer, java.lang.Object>> targetMap = null;
				Integer[] portRange = null;
			String myRange = null;
			boolean found = false;
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Entering .. OTN card=" + cardType + " shelf=" + shelf + " portGroup=" + portGroup);

			if ("OSLM12".equalsIgnoreCase(cardType))
				targetMap = portRulesPG12;
			else if ("OSLM48".equalsIgnoreCase(cardType))
				targetMap = portRulesPG48;
			else if ("OSLM50".equalsIgnoreCase(cardType))
				targetMap = portRulesPG50;

			if (targetMap == null || shelf == null)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "bad input or OTN portRules not initialized");
				return null;
			}

			if (targetMap.containsKey(shelf))
			{
				HashMap<Integer, java.lang.Object> portMap = targetMap.get(shelf);
				Integer inPortGroup = new Integer(portGroup);
				if (portMap.containsKey(inPortGroup))
				{
					portRange = (Integer[]) portMap.get(inPortGroup);
					if (portRange.length == 2)
					{
						myRange = portRange[0].intValue() + "-" + portRange[1].intValue();
						found = true;
					}
				}
			}

			if (found)
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "Leaving .. OTN card=" + cardType + " shelf=" + shelf + " portRange=" + myRange);
			}
			else
			{
				TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, "No port range found for card=" + cardType + " shelf=" + shelf  + " portGroup=" + portGroup);
			}
			return portRange;
		}

		public static void initEqtypeOTN() throws Exception
		{
			String funcName = "initEqtypeOTN";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering....");
			properties = new Properties();
			String fileName = PropertyHandler.getValue("EQTYPE.OTN");
			if (fileName == null)
			{
				throw new Exception("ERROR: EQTYPE.OTN is not defined");
			}
			try
			{
				properties.load(new FileInputStream(fileName));
			}
			catch (Exception e) 
			{ 
				throw e; 
			}
		}

		public static String determineCardCategoryForOTN(String type)
		{
			String funcName = "determineCardCategoryForOTN";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Entering.... type=" + type);
			String otnCardType = new String();
	        String nise_oslm3 = properties.getProperty("OTN_NISE_OSLM3_CARD");
			String nise_oslm12 = properties.getProperty("OTN_NISE_OSLM12_CARD");
			String nise_oslm48 = properties.getProperty("OTN_NISE_OSLM48_CARD");
			String nise_oslm5 = properties.getProperty("OTN_NISE_OSLM5_CARD");
			String nise_oslm50 = properties.getProperty("OTN_NISE_OSLM50_CARD");
			String ems_oslm3 = properties.getProperty("OTN_EMS_OSLM3_CARD");
			String ems_oslm12 = properties.getProperty("OTN_EMS_OSLM12_CARD");
			String ems_oslm48 = properties.getProperty("OTN_EMS_OSLM48_CARD");
			String ems_oslm5 = properties.getProperty("OTN_EMS_OSLM5_CARD");
			String ems_oslm50 = properties.getProperty("OTN_EMS_OSLM50_CARD");

			if (isOneInTwo(type, nise_oslm3) || isOneInTwo(type, ems_oslm3))
				otnCardType = "OSLM3";
			else if (isOneInTwo(type, nise_oslm12) || isOneInTwo(type, ems_oslm12))
				otnCardType = "OSLM12";
			else if (isOneInTwo(type, nise_oslm48) || isOneInTwo(type, ems_oslm48))
				otnCardType = "OSLM48";
			else if (isOneInTwo(type, nise_oslm5) || isOneInTwo(type, ems_oslm5))
				otnCardType = "OSLM5";
			else if (isOneInTwo(type, nise_oslm50) || isOneInTwo(type, ems_oslm50))
				otnCardType = "OSLM50";
			TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, "Leaving.... otnCardType=" + otnCardType);
			return otnCardType;
		}
		
		// AID conversion for Lumentum Bidirectional ports(291662):
			// For services having bidireactional port, tidaid queries need to be supported for both ends.
			// This method convert the LUM intra/inter ports based on the fixed mapping between them (A-B, C-D, E-F).
			public static String aidConversion(String aid)
			{
						String funcName = "aidConversion with aid ";
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_1, 
						"Entering...,  , AID=" + aid);

					String ports = "A,B,C,D,E,F";
					String tempAid = new String();

					StringTokenizer st = new StringTokenizer(aid, "-");
					int tokenSize = st.countTokens();
					if (tokenSize < 3)
					{
						TLevels.log.trace1(_SRC_, funcName, TLevels.TR_LVL_6, 
						"bad Aid !!"+aid);
				                       return tempAid;
					}
			   //1-1-A
					while (st.hasMoreTokens())
					{
						String token = st.nextToken();
						if (CommonUtil.isOneStartsWithTwo(token, ports))
						{
							if(token.startsWith("A"))
								token = "B";
							else if(token.startsWith("B"))
								token="A";
							else if(token.startsWith("C"))
								token="D";
							else if(token.startsWith("D"))
								token="C";
							else if(token.startsWith("E"))
								 token="F";
							else if (token.startsWith("F"))
								token="E";
							
						}
						if(tempAid.length()>0)
					  		tempAid=tempAid+ "-"+token;
						else
							tempAid=token;
						}
						TLevels.log.trace1(_SRC_, "doJob", TLevels.TR_LVL_1, 
									"It's tempAid, =" + tempAid );
							return tempAid;
					}
	}



}
