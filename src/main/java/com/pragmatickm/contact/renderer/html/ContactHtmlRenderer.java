/*
 * pragmatickm-contact-renderer-html - Contacts rendered as HTML in a Servlet environment.
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaWriter;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.html.Html;
import com.aoindustries.io.buffer.BufferResult;
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

	private static void writeRow(String header, String value, Html html) throws IOException {
		if(value != null) {
			html.out.write("<tr><th>");
			html.text(header);
			html.out.write("</th><td colspan=\"2\">");
			html.text(value);
			html.out.write("</td></tr>\n");
		}
	}

	public static void writeContactTable(
		PageIndex pageIndex,
		Html html,
		ElementContext context,
		Object style,
		Contact contact
	) throws IOException {
		html.out.write("<table id=\"");
		PageIndex.appendIdInPage(
			pageIndex,
			contact.getPage(),
			contact.getId(),
			new MediaWriter(textInXhtmlAttributeEncoder, html.out)
		);
		html.out.write("\" class=\"ao-grid pragmatickm-contact\"");
		if(style != null) {
			html.out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, html.out);
			html.out.write('"');
		}
		html.out.write(">\n");
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
			html.out.write("<thead><tr><th colspan=\"3\"><div>");
			contact.appendLabel(new MediaWriter(textInXhtmlEncoder, html.out));
			html.out.write("</div></th></tr></thead>\n");
		}
		html.out.write("<tbody>\n");
		writeRow("Title:", title, html);
		writeRow("First:", first, html);
		writeRow("Middle:", middle, html);
		writeRow("Nick:", nick, html);
		writeRow("Last:", last, html);
		writeRow("Maiden:", maiden, html);
		writeRow("Suffix:", suffix, html);
		writeRow("Company:", company, html);
		writeRow("Department:", department, html);
		writeRow("Job Title:", jobTitle, html);
		for(Email email : emails) {
			String emailString = email.toString();
			html.out.write("<tr><th>Email:</th><td colspan=\"2\"><div class=\"pragmatickm-contact-email\"><a href=\"mailto:");
			encodeTextInXhtmlAttribute(emailString, html.out);
			html.out.write("\">");
			html.text(emailString);
			html.out.write("</a></div></td></tr>\n");
		}
		for(PhoneNumber phoneNumber : phoneNumbers) {
			PhoneType type = phoneNumber.getType();
			String number = phoneNumber.getNumber();
			String comment = phoneNumber.getComment();
			html.out.write("<tr><th>");
			html.text(type.getLabel());
			html.out.write(":</th><td");
			if(comment==null) html.out.write(" colspan=\"2\"");
			html.out.write("><div class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), html.out);
			html.out.write("\"><a href=\"tel:");
			encodeTextInXhtmlAttribute(number.replace(' ', '-'), html.out);
			html.out.write("\">");
			html.text(number);
			html.out.write("</a></div></td>");
			if(comment!=null) {
				html.out.write("<td>");
				html.text(comment);
				html.out.write("</td>");
			}
			html.out.write("</tr>\n");
		}
		for(Im im : ims) {
			ImType type = im.getType();
			String handle = im.getHandle();
			String comment = im.getComment();
			html.out.write("<tr><th>");
			html.text(type.getLabel());
			html.out.write(":</th><td");
			if(comment==null) html.out.write(" colspan=\"2\"");
			html.out.write("><div class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), html.out);
			html.out.write("\">");
			html.text(handle);
			html.out.write("</div></td>");
			if(comment!=null) {
				html.out.write("<td>");
				html.text(comment);
				html.out.write("</td>");
			}
			html.out.write("</tr>\n");
		}
		for(String webPage : webPages) {
			html.out.write("<tr><th>Web Page:</th><td colspan=\"2\"><div class=\"pragmatickm-contact-web-page\"><a href=\"");
			encodeTextInXhtmlAttribute(webPage, html.out);
			html.out.write("\">");
			html.text(webPage);
			html.out.write("</a></div></td></tr>\n");
		}
		for(Address address : addresses) {
			AddressType type = address.getType();
			html.out.write("<tr><th class=\"");
			encodeTextInXhtmlAttribute(type.getCssClass(), html.out);
			html.out.write("\" colspan=\"3\"><div>");
			html.text(type.getLabel());
			html.out.write("</div></th></tr>\n");
			writeRow("Address 1:", address.getAddress1(), html);
			writeRow("Address 2:", address.getAddress2(), html);
			writeRow("City:", address.getCity(), html);
			writeRow("State/Prov:", address.getStateProv(), html);
			writeRow("ZIP/Postal:", address.getZipPostal(), html);
			writeRow("Country:", address.getCountry(), html);
			writeRow("Comment:", address.getComment(), html);
		}
		BufferResult body = contact.getBody();
		if(body.getLength() > 0) {
			html.out.write("<tr><td class=\"pragmatickm-contact-body\" colspan=\"3\">");
			body.writeTo(new NodeBodyWriter(contact, html.out, context));
			html.out.write("</td></tr>\n");
		}
		html.out.write("</tbody>\n"
				+ "</table>");
	}

	/**
	 * Make no instances.
	 */
	private ContactHtmlRenderer() {
	}
}
