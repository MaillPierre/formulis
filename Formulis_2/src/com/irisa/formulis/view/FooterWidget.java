package com.irisa.formulis.view;

import com.github.gwtbootstrap.client.ui.Footer;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class FooterWidget extends Composite {

	private Footer element = new Footer();
//	 <p> -->
//	 <!--       Created by -->
//	 <!--       <a target="_blank" href="http://www.irisa.fr/LIS/Members/ferre/">Sébastien Ferré</a> and Pierre Maillot © 2016 -->
//	 <!--       <a target="_blank" href="http://www.irisa.fr/LIS/"><img src="http://www.irisa.fr/LIS/ferre/sewelis/lis.png" alt="LIS team" height="40"></a> -->
//	 <!--       <a target="_blank" href="http://www.irisa.fr/"><img src="http://www.irisa.fr/LIS/ferre/sewelis/irisa.jpg" alt="IRISA" height="40"></a> -->
//	 <!--       <a target="_blank" href="http://www.univ-rennes1.fr/"><img src="http://www.irisa.fr/LIS/ferre/sewelis/UR1.png" alt="Université Rennes 1" height="40"></a> -->
//	 <!--     </p> -->
//	 <!--     <p> -->
//	 <!--       Visit -->
//	 <!--       <a target="_blank" href="http://www.irisa.fr/LIS/ferre/"> -->
//	 <!-- 	Sébastien Ferré's homepage -->
//	 <!--       </a> -->
//	 <!--       for more information. -->
//	 <!--     </p> -->
	
	public FooterWidget() {
		initWidget(element);
		
		Paragraph createdP = new Paragraph();
		
		Image lisImage = new Image("http://www.irisa.fr/LIS/ferre/sewelis/lis.png");
		lisImage.setHeight("40px");
		Image irisaImage = new Image("http://www.irisa.fr/LIS/ferre/sewelis/irisa.jpg");
		irisaImage.setHeight("40px");
		Image ur1Image = new Image("http://www.irisa.fr/LIS/ferre/sewelis/UR1.png");
		ur1Image.setHeight("40px");
		
//		Anchor sebLink = new Anchor("http://www.irisa.fr/LIS/Members/ferre/");
//		sebLink.setText("Sébastien Ferré");
		InlineHTML sebLink = new InlineHTML(SafeHtmlUtils.fromTrustedString("<a target=\"_blank\" href=\"http://www.irisa.fr/LIS/Members/ferre/\">Sébastien Ferré</a>"));
//		Anchor lisLink = new Anchor("http://www.irisa.fr/LIS/");
//		lisLink.getElement().setInnerText("");
//		lisLink.getElement().appendChild(lisImage.getElement());
		InlineHTML lisLink = new InlineHTML(SafeHtmlUtils.fromTrustedString("<a target=\"_blank\" href=\"http://www.irisa.fr/LIS/\"><img src=\"http://www.irisa.fr/LIS/ferre/sewelis/lis.png\" alt=\"LIS team\" style=\"height: 20px;\"></a>"));
//		Anchor irisaLink = new Anchor("http://www.irisa.fr/");
//		irisaLink.getElement().setInnerText("");
//		irisaLink.getElement().appendChild(irisaImage.getElement());
		InlineHTML irisaLink = new InlineHTML(SafeHtmlUtils.fromTrustedString("<a target=\"_blank\" href=\"http://www.irisa.fr/\"><img src=\"http://www.irisa.fr/LIS/ferre/sewelis/irisa.jpg\" alt=\"IRISA\" style=\"height: 20px;\"></a>"));
//		Anchor ur1Link = new Anchor("http://www.univ-rennes1.fr/");
//		ur1Link.getElement().setInnerText("");
//		ur1Link.getElement().appendChild(ur1Image.getElement());
		InlineHTML ur1Link = new InlineHTML(SafeHtmlUtils.fromTrustedString("<a target=\"_blank\" href=\"http://www.univ-rennes1.fr/\"><img src=\"http://www.irisa.fr/LIS/ferre/sewelis/UR1.png\" alt=\"Université Rennes 1\" style=\"height: 20px;\"></a>"));
		
		createdP.setText("Created by Pierre Maillot and ");
		createdP.add(sebLink);
		createdP.add(new InlineLabel(" © 2016 "));
		createdP.add(lisLink);
		createdP.add(irisaLink);
		createdP.add(ur1Link);
		
		
		element.add(createdP);
	}
	
}
