package base.util;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MyPrediction {
	
	String description;
	String place_id;
	String id;
	String[] matched_substrings;
	String reference;
	String[] terms;
	String[] preTypes;
	
	private String formatted_address;
	private boolean partial_match;
	
	private JsonGoogleResponsePlaceGeometry geometry;

	@JsonIgnore
	private Object address_components;
	@JsonIgnore
	private Object types;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPlace_id() {
		return place_id;
	}
	public void setPlace_id(String place_id) {
		this.place_id = place_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String[] getMatched_substrings() {
		return matched_substrings;
	}
	public void setMatched_substrings(String[] matched_substrings) {
		this.matched_substrings = matched_substrings;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String[] getTerms() {
		return terms;
	}
	public void setTerms(String[] terms) {
		this.terms = terms;
	}
	public String[] getPreTypes() {
		return preTypes;
	}
	public void setPreTypes(String[] preTypes) {
		this.preTypes = preTypes;
	}
	public Object getAddress_components() {
		return address_components;
	}
	public void setAddress_components(Object address_components) {
		this.address_components = address_components;
	}
	public Object getType() {
		return types;
	}
	public void setType(Object type) {
		this.types = type;
	}
	public String getFormatted_address() {
		return formatted_address;
	}
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	public boolean isPartial_match() {
		return partial_match;
	}
	public void setPartial_match(boolean partial_match) {
		this.partial_match = partial_match;
	}
	public JsonGoogleResponsePlaceGeometry getGeometry() {
		return geometry;
	}
	public void setGeometry(JsonGoogleResponsePlaceGeometry geometry) {
		this.geometry = geometry;
	}
	
}
