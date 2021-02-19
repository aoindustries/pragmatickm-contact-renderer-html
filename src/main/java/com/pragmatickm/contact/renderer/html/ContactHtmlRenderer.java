/*
 * pragmatickm-contact-renderer-html - Contacts rendered as HTML in a Servlet environment.
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of pragmatickm-contact-renderer-html.
 *
 * pragmatickm-contact-renderer-html is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pragmatickm-contact-renderer-html is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with pragmatickm-contact-renderer-html.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pragmatickm.contact.renderer.html;

import com.aoindustries.encoding.MediaWriter;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.html.Document;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.lang.Coercion;
import com.aoindustries.net.Email;
import com.pragmatickm.contact.model.Address;
import com.pragmatickm.contact.model.AddressType;
import com.pragmatickm.contact.model.Contact;
import com.pragmatickm.contact.model.Im;
import com.pragmatickm.contact.model.ImType;
import com.pragmatickm.contact.model.PhoneNumber;
import com.pragmatickm.contact.model.PhoneType;
import com.semanticcms.core.model.ElementContext;
import com.semanticcms.core.model.NodeBodyWriter;
import com.semanticcms.core.renderer.html.PageIndex;
import java.io.IOException;
import java.util.List;

final public class ContactHtmlRenderer {

	private static void writeRow(String header, String value, Document document) throws IOException {
		if(value != null) {
			document.out.write("<tr><th>");
			document.text(header);
			document.out.write("</th><td colspan=\"2\">");
			document.text(value);
			document.out.write("</td></tr>\n");
		}
	}

	public static void writeContactTable(
		PageIndex pageIndex,
		Document document,
		ElementContext context,
		Object style,
		Contact contact
	) throws IOException {
		document.out.write("<table id=\"");
		PageIndex.appendIdInPage(
			pageIndex,
			contact.getPage(),
			contact.getId(),
			new MediaWriter(document.encodingContext, textInXhtmlAttributeEncoder, document.out)
		);
		document.out.write("\" class=\"ao-grid pragmatickm-contact\"");
		if(style != null) {
			document.out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, document.out);
			document.out.write('"');
		}
		document.out.write(">\n");
		String title = contact.getTitle();
		String first = contact.getFirst();
		String middle = contact.getMiddle();
		String nick = contact.getNick();
		String last = contact.getLast();
		String maiden = contact.getMaiden();
		String suffix = contact.getSuffix();
		String jobTitle = contact.getJobTitle();
		String company = contact.getCompany();
		String department = contact.getDepartment();
		List<Email> emails = contact.getEmails();
		List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
		List<Im> ims = contact.getIms();
		List<String> webPages = contact.getWebPages();
		List<Address> addresses = contact.getAddresses();
		// Hide header for address-only view
		if(
			title != null
			|| first != null
			|| middle != null
			|| nick != null
			|| last != null
			|| maiden != null
			|| suffix != null
			|| jobTitle != null
			|| company != null
			|| department != null
			|| !emails.isEmpty()
			|| !phoneNumbers.isEmpty()
			|| !ims.isEmpty()
			|| !webPages.isEmpty()
			|| addresses.isEmpty() // When no addresses, always display with a full contact header
		) {
			document.out.write("<thead><tr><th colspan=\"3\"><div>");
			document.text(contact.getLabel());
			document.out.write("</div></th></tr></thead>\n");
		}
		document.out.write("<tbody>\n");
		writeRow("Title:", title, document);
		writeRow("First:", first, document);
		writeRow("Middle:", middle, document);
		writeRow("Nick:", nick, document);
		writeRow("Last:", last, document);
		writeRow("Maiden:", maiden, document);
		writeRow("Suffix:", suffix, document);
		writeRow("Company:", company, document);
		writeRow("Department:", department, document);
		writeRow("Job Title:", jobTitle, document);
		for(Email email : emails) {
			String emailString = email.toString();
			document.out.write("<tr><th>Email:</th><td colspan=\"2\"><div class=\"pragmatickm-contact-email\"><a href=\"mailto:");
			encodeTextInXhtmlAttribute(emailString, document.out);
			document.out.write("\">");
			document.text(emailString);
			document.out.write("</a></div></td></tr>\n");
		}
		for(PhoneNumber phoneNumber : phoneNumbers) {
			PhoneType type = phoneNumber.getType();
			String number = phoneNumber.getNumber();
			String comment = phoneNumber.getComment();
			document.out.write("<tr><th>");
			document.text(type.getLabel());
			document.out.write(":</th><td");
			if(comment==null) document.out.write(" colspan=\"2\"");
			document.out.write("><div class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), document.out);
			document.out.write("\"><a href=\"tel:");
			encodeTextInXhtmlAttribute(number.replace(' ', '-'), document.out);
			document.out.write("\">");
			document.text(number);
			document.out.write("</a></div></td>");
			if(comment!=null) {
				document.out.write("<td>");
				document.text(comment);
				document.out.write("</td>");
			}
			document.out.write("</tr>\n");
		}
		for(Im im : ims) {
			ImType type = im.getType();
			String handle = im.getHandle();
			String comment = im.getComment();
			document.out.write("<tr><th>");
			document.text(type.getLabel());
			document.out.write(":</th><td");
			if(comment==null) document.out.write(" colspan=\"2\"");
			document.out.write("><div class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), document.out);
			document.out.write("\">");
			document.text(handle);
			document.out.write("</div></td>");
			if(comment!=null) {
				document.out.write("<td>");
				document.text(comment);
				document.out.write("</td>");
			}
			document.out.write("</tr>\n");
		}
		for(String webPage : webPages) {
			document.out.write("<tr><th>Web Page:</th><td colspan=\"2\"><div class=\"pragmatickm-contact-web-page\"><a href=\"");
			encodeTextInXhtmlAttribute(webPage, document.out);
			document.out.write("\">");
			document.text(webPage);
			document.out.write("</a></div></td></tr>\n");
		}
		for(Address address : addresses) {
			AddressType type = address.getType();
			document.out.write("<tr><th class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), document.out);
			document.out.write("\" colspan=\"3\"><div>");
			document.text(type.getLabel());
			document.out.write("</div></th></tr>\n");
			writeRow("Address 1:", address.getAddress1(), document);
			writeRow("Address 2:", address.getAddress2(), document);
			writeRow("City:", address.getCity(), document);
			writeRow("State/Prov:", address.getStateProv(), document);
			writeRow("ZIP/Postal:", address.getZipPostal(), document);
			writeRow("Country:", address.getCountry(), document);
			writeRow("Comment:", address.getComment(), document);
		}
		BufferResult body = contact.getBody();
		if(body.getLength() > 0) {
			document.out.write("<tr><td class=\"pragmatickm-contact-body\" colspan=\"3\">");
			body.writeTo(new NodeBodyWriter(contact, document.out, context));
			document.out.write("</td></tr>\n");
		}
		document.out.write("</tbody>\n"
				+ "</table>");
	}

	/**
	 * Make no instances.
	 */
	private ContactHtmlRenderer() {
	}
}
