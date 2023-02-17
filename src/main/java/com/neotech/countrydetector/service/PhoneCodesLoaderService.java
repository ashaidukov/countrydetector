package com.neotech.countrydetector.service;

import com.neotech.countrydetector.service.model.Country;
import com.neotech.countrydetector.service.model.PhonePrefixCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneCodesLoaderService {

    private static final String ROWSPAN_ATTR = "rowspan";
    private static final String COMPLEX_ROW_ROWSPAN = "2";
    private static final String WIKITABLE_CLASS = "wikitable";
    private static final String TITLE_ATTR = "title";
    private static final String BR_TAG = "br";
    private static final String A_TAG = "a";
    private static final String P_TAG = "p";
    private static final String AMBIGUOUS_CODE = "ambig.";
    private static final String NOT_ASSIGNED_CODE = "—";
    private static final String NON_COUNTRY_SPECIFIC_CODE = "**";

    private final DataService dataService;

    @PostConstruct
    public void loadPhoneCodes() throws Exception {
        Document document = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_country_calling_codes").get();
        Elements phoneCodeTableRows = findPhoneCodesTableRows(document);
        List<PhonePrefixCode> prefixCodes = new ArrayList<>();
        for (int i = 1; i < phoneCodeTableRows.size() - 1; i++) {
            Element row = phoneCodeTableRows.get(i);
            if (isEmptyRow(row)) {
                continue;
            }
            if (isComplexRow(row)) {
                i++;
                if (isNorthAmericaCodesRow(row)) {
                    var americasRow = phoneCodeTableRows.get(i);
                    prefixCodes.addAll(processNorthAmericaCodesRow(americasRow));
                } else {
                    prefixCodes.addAll(processRegularPhoneCodesRow(phoneCodeTableRows.get(i)));
                }
            } else {
                prefixCodes.addAll(processRegularPhoneCodesRow(row));
            }
        }
        dataService.clearPhoneCodes();
        dataService.savePhoneCodes(prefixCodes);
    }

    private boolean isEmptyRow(Element row) {
        return row.firstElementChild().text().isEmpty();
    }
    private boolean isComplexRow(Element row) {
        return row.firstElementChild().attr(ROWSPAN_ATTR).equals(COMPLEX_ROW_ROWSPAN);
    }

    private boolean isNorthAmericaCodesRow(Element row) {
        return row.firstElementChild().text().equals("1x");
    }

    private Elements findPhoneCodesTableRows(Document document) {
        var wikiTables = document.getElementsByClass(WIKITABLE_CLASS);
        for (Element table : wikiTables) {
            var tBody = table.child(0);
            var headerRow = tBody.firstElementChild();
            var headerContent = headerRow.children().get(1).text();
            if (headerContent.equals("x = 0")) {
                return tBody.children();
            }
        }
        return null;
    }

    private List<PhonePrefixCode> processNorthAmericaCodesRow(Element row) {
        var pTags = row.getElementsByTag(P_TAG);
        List<PhonePrefixCode> prefixCodes = new ArrayList<>();
        for (Element pTag: pTags) {
            prefixCodes.addAll(processNorthAmericaCell(pTag));
        }
        return prefixCodes;
    }

    private List<PhonePrefixCode> processNorthAmericaCell(Element pTag) {
        List<PhonePrefixCode> phonePrefixCodes = new ArrayList<>();
        String code = null;
        String countryCode = null;
        String country = null;
        for (int i = 0; i < pTag.children().size(); i++) {
            if (pTag.children().get(i).tagName().equals(A_TAG)) {
                var textInTag = pTag.children().get(i).text();
                if (textInTag.startsWith("+")) {
                    code = textInTag.replaceAll("[^\\+0-9]", "");
                } else if (textInTag.length() == 2) {
                    country = pTag.children().get(i).attr(TITLE_ATTR);
                    countryCode = textInTag;
                }
            } else if (pTag.children().get(i).tagName().equals(BR_TAG)) {
                phonePrefixCodes.add(PhonePrefixCode.builder()
                        .prefixCode(code)
                        .countries(Set.of(Country.builder()
                                .code(countryCode)
                                .name(country)
                                .build()))
                        .build());
                code = null;
            }
        }
        if (code != null) {
            phonePrefixCodes.add(PhonePrefixCode.builder()
                    .prefixCode(code)
                    .countries(Set.of(Country.builder()
                            .code(countryCode)
                            .name(country)
                            .build()))
                    .build());
        }
        return phonePrefixCodes;
    }

    private List<PhonePrefixCode> processRegularPhoneCodesRow(Element row) {
        List<PhonePrefixCode> phonePrefixCodes = new ArrayList<>();
        for (int i = 1; i < row.childrenSize(); i++) {
            var phonePrefixCode = processRegularCell(row.children().get(i));
            if (phonePrefixCode != null) {
                phonePrefixCodes.add(phonePrefixCode);
            }
        }
        return phonePrefixCodes;
    }

    private PhonePrefixCode processRegularCell(Element cell) {
        var cellText = cell.text();
        if (!cellText.contains(AMBIGUOUS_CODE)
                && !cellText.contains(NOT_ASSIGNED_CODE)
                && !cellText.contains(NON_COUNTRY_SPECIFIC_CODE)) {
            List<Element> hrefs = cell.getElementsByTag(A_TAG);
            Set<Country> countries = new HashSet<>();
            for (int i = 1; i < hrefs.size(); i++) {
                var hrefWithCountryInfo = hrefs.get(i);
                countries.add(Country.builder()
                        .name(hrefWithCountryInfo.attr(TITLE_ATTR))
                        .code(hrefWithCountryInfo.text())
                        .build());
            }
            return PhonePrefixCode.builder()
                    .prefixCode(hrefs.get(0).text().replaceAll("[^\\+0-9]", ""))
                    .countries(countries)
                    .build();
        }
        return null;
    }
}
