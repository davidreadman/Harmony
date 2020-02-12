/* The setup data for this at the beginning of JFrameGuiActions looked like it could become massive
* Shifted this data here with the possible future extension of putting the 2525B/C to 2525D data in here in the future*/
public class SymbolModifierData
{
    public static final String[] iFFStrings = {"FRIEND", "HOSTILE", "NEUTRAL"};
    public static final String[] iFFIDStrings = {"F", "H", "N"};
    public static final char[] iFFChars = {'F', 'H', 'N'};
    //http://www.mapsymbs.com/ms2525c.pdf pg 52
    String[] hQStrings = {"Not","HQ","TFHQ"};
    String[] hQIDStrings = {"-","A","B"};
    char[] hQChars = {'-', 'A', 'B'};

    String[] levelStrings = {"Null","Team","Squad","Section","Platoon","Company","Battalion","Regiment","Brigade"};
    String[] levelIDStrings = {"-","A","B","C","D","E","F","G","H"};
    char[] levelChars = {'-', 'A', 'B','C','D','E','F','G','H'};
    /*this string in its entirety would be the entire table of:  //http://www.mapsymbs.com/ms2525c.pdf pg 55
    This minimal implementation can be expanded at a later time
     */
    String[] functionStrings=
            {
            "Ground Track",
            "Ground Track-Unit",
            "Ground Track-Unit-Combat",
            "Ground Track-Unit-Combat-Air Defence",
            "Ground Track-Unit-Combat-Armor",
            "Ground Track-Unit-Combat-Aviation",
            "Ground Track-Unit-Combat-Infantry",
            "Ground Track-Unit-Combat-Engineer",
            "Ground Track-Unit-Combat-Field Artillery",
            "Ground Track-Unit-Combat-Recon",
                    "Ground Track-Unit-Combat-Recon-Cavalry-Armored",
            "Ground Track-Unit-Combat-Missile",
            "Ground Track-Unit-Combat-Internal Security Forces"
            };
    //this is a set of 6 chars used in the 2525Bid from Position 5 through to 10
    String[] functionIDStrings=
            {
            "------",//"Ground Track",
            "U-----",//"Ground Track-Unit",
            "UC----",//"Ground Track-Unit-Combat",
            "UCD---",//,"Ground Track-Unit-Combat-Air Defence",
            "UCA",//"Ground Track-Unit-Combat-Armor",
            "UCV---",// "Ground Track-Unit-Combat-Aviation",
            "UCI---",// "Ground Track-Unit-Combat-Infantry",
            "UCE---",//"Ground Track-Unit-Combat-Engineer",
            "UCF---",//"Ground Track-Unit-Combat-Field Artillery",
            "UCR---",// "Ground Track-Unit-Combat-Recon",
                    "UCRVA-",// "Ground Track-Unit-Combat-Recon-Cavalry Armored",
            "UCM---",// "Ground Track-Unit-Combat-Missile",
            "UCS---",// "Ground Track-Unit-Combat-Internal Security Forces"
             };
}
