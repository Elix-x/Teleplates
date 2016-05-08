package code.elix_x.mods.teleplates.names;

import java.util.Random;

public class TeleplatesRandomName {

	public static final String[] names = new String[]{"Whipcrane Crag", "Wilderthrone Valley", "Batbead Mire", "Oxelf County", "Hintwept Parish", "Judgeheal Village", "Maekath City", "Maskhair Mountain", "Mesor Lake", "Neamai Road", "Graperoad Isle", "Bathwarm Grassland", "Idishual Jungle", "Tukr Bluffs", "Tunc Beach", "Noukyn Canyon", "Growstag Beach", "Notetrout City", "Lawpane Sea", "Viv Forest", "Snakelead Creek", "Fr'nn Parish", "Marchbreeze Crossings", "Th'k Swamps", "Measim Islands", "Hingesteer Dale", "Beechpipe Cave", "Lampcatch Outpost", "Scul Hill", "Ero'anus Jungle", "Mugnear Lake", "Adeanual Stream", "Jaess Swamps", "Paraelri Canyon", "Catchstag Glen", "Jaezelae Brook", "Peeproost City", "Frall Lake", "Ci'o Falls", "Tornram Canyon", "Fandirge Hill", "Oakrice Straits", "Swordrot Island", "Oroury Dale", "Testwarn Outpost", "Waterheir Mere", "Shellnoon Foothill", "Crylast Creek", "Nidu Isles", "Faerienote Creek", "Cranu Falls", "Groveroof Grassland", "Frynaxor Heights", "Skinhigh Canyon", "Cuvaelus Castle", "Oxcrop Peak", "Roguedug Parish", "Cardcall Pass", "Crowlich Lagoon", "Caly Lake", "Larolo Fortress", "Mea'imao Dale", "Versesilver Coast", "Tramak Town", "Weeksmoke Swamp", "Scaledoor Village", "Erou'ae Isle", "Hookhawk River", "Panetoad Island", "Costgem Canyon", "Leatherlaw Creek", "Corpseewe Volcano", "Noonwheel Canyon", "Gladfun Sea", "Griefmilk River", "Sunlad Lake", "Tever Creek", "Storejuice Grassland", "Brownround Shire", "Vyrel Wood", "Cleanwine Dale", "Packpick Crag", "Feyhob Woods", "Ker Stream", "Quartztap Ruins", "Orea'elus Fortress", "Criven River", "Seascream Crossing", "Lutespeed Volcano", "Vi'ar Foothills", "Rimblast River", "Tornnumb Coasts", "Tae'or Lagoon", "M'rus Mountain", "Sha'us Road", "Healstair Village", "Anashra Glad", "Nainak Canyon", "Ninthmale Valley", "Wyrmboy Plain"};

	public static String next(Random random){
		return names[random.nextInt(names.length)];
	}

}
