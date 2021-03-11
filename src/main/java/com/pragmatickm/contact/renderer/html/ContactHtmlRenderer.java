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

import com.aoindustries.html.AnyDocument;
import com.aoindustries.html.PalpableContent;
import com.aoindustries.html.TR_factory;
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

	private static void writeRow(String header, String value, TR_factory<?, ?> factory) throws IOException {
		if(value != null) {
			factory.tr__(tr -> tr
				.th__(header)
				.td().colspan(2).__(value)
			);
		}
	}

	public static <
		D extends AnyDocument<D>,
		__ extends PalpableContent<D, __>
	> void writeContactTable(
		PageIndex pageIndex,
		__ content,
		ElementContext context,
		Object style,
		Contact contact
	) throws IOException {
		content.table()
			.id(idAttr -> PageIndex.appendIdInPage(
				pageIndex,
				contact.getPage(),
				contact.getId(),
				idAttr
			))
			.clazz("ao-grid", "pragmatickm-contact")
			.style(style)
		.__(table -> {
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
				table.thead__(thead -> thead
					.tr__(tr -> tr
						.th().colspan(3).__(th -> th
							.div__(contact)
						)
					)
				);
			}
			table.tbody__(tbody -> {
				writeRow("Title:", title, tbody);
				writeRow("First:", first, tbody);
				writeRow("Middle:", middle, tbody);
				writeRow("Nick:", nick, tbody);
				writeRow("Last:", last, tbody);
				writeRow("Maiden:", maiden, tbody);
				writeRow("Suffix:", suffix, tbody);
				writeRow("Company:", company, tbody);
				writeRow("Department:", department, tbody);
				writeRow("Job Title:", jobTitle, tbody);
				for(Email email : emails) {
					String emailString = email.toString();
					tbody.tr__(tr -> tr
						.th__("Email:")
						.td().colspan(2).__(td -> td
							.div().clazz("pragmatickm-contact-email").__(div -> div
								.a("mailto:" + emailString).__(emailString)
							)
						)
					);
				}
				for(PhoneNumber phoneNumber : phoneNumbers) {
					PhoneType type = phoneNumber.getType();
					String number = phoneNumber.getNumber();
					String comment = phoneNumber.getComment();
					tbody.tr__(tr -> {
						tr.th__(th -> th
							.text(type.getLabel()).text(':')
						).td().colspan(comment == null ? 2 : 1).__(td -> td
							.div().clazz(type.getCssClass()).__(div -> div
								.a("tel:" + number.replace(' ', '-')).__(number)
							)
						);
						if(comment != null) {
							tr.td__(comment);
						}
					});
				}
				for(Im im : ims) {
					ImType type = im.getType();
					String handle = im.getHandle();
					String comment = im.getComment();
					tbody.tr__(tr -> {
						tr.th__(th -> th
							.text(type.getLabel()).text(':')
						)
						.td().colspan(comment == null ? 2 : 1).__(td -> td
							.div().clazz(type.getCssClass()).__(handle)
						);
						if(comment != null) {
							tr.td__(comment);
						}
					});
				}
				for(String webPage : webPages) {
					tbody.tr__(tr -> tr
						.th__("Web Page:")
						.td().colspan(2).__(td -> td
							.div().clazz("pragmatickm-contact-web-page").__(div -> div
								.a(webPage).__(webPage)
							)
						)
					);
				}
				for(Address address : addresses) {
					AddressType type = address.getType();
					tbody.tr__(tr -> tr
						.th().clazz(type.getCssClass()).colspan(3).__(th -> th
							.div__(type.getLabel())
						)
					);
					writeRow("Address 1:", address.getAddress1(), tbody);
					writeRow("Address 2:", address.getAddress2(), tbody);
					writeRow("City:", address.getCity(), tbody);
					writeRow("State/Prov:", address.getStateProv(), tbody);
					writeRow("ZIP/Postal:", address.getZipPostal(), tbody);
					writeRow("Country:", address.getCountry(), tbody);
					writeRow("Comment:", address.getComment(), tbody);
				}
				BufferResult body = contact.getBody();
				if(body.getLength() > 0) {
					tbody.tr__(tr -> tr
						.td().clazz("pragmatickm-contact-body").colspan(3).__(td ->
							body.writeTo(new NodeBodyWriter(contact, td.getDocument().out, context))
						)
					);
				}
			});
		});
	}

	/**
	 * Make no instances.
	 */
	private ContactHtmlRenderer() {
	}
}
