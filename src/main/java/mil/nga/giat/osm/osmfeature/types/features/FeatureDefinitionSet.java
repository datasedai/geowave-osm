package mil.nga.giat.osm.osmfeature.types.features;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import mil.nga.giat.geowave.adapter.vector.FeatureDataAdapter;
import mil.nga.giat.geowave.core.index.StringUtils;
import mil.nga.giat.osm.osmfeature.FeatureConfigParser;
import mil.nga.giat.osm.osmfeature.types.attributes.AttributeDefinition;
import mil.nga.giat.osm.osmfeature.types.attributes.AttributeType;
import mil.nga.giat.osm.osmfeature.types.attributes.AttributeTypes;

public class FeatureDefinitionSet {
	public final static List<String> GeneralizedFeatures = new ArrayList<>();
	public final static List<FeatureDefinition> Features = new ArrayList<>();
	public final static Map<String, FeatureDataAdapter> featureAdapters = new HashMap<>();
	public final static Map<String, SimpleFeatureType> featureTypes = new HashMap<>();
	private final static Object MUTEX = new Object();
	private static boolean initialized = false;
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureDefinitionSet.class);

	public static void initialize(String configFile){
		synchronized (MUTEX) {
			if (!initialized) {
				FeatureConfigParser fcp = new FeatureConfigParser();
				ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes(StringUtils.GEOWAVE_CHAR_SET));
				try {
					fcp.parseConfig(bais);
				}
				catch (IOException e) {
					LOGGER.error("Unable to parse config file string", e);
				}
				finally {
					IOUtils.closeQuietly(bais);
				}

				for (FeatureDefinition fd : Features){
					parseFeatureDefinition(fd);
				}

				initialized = true;
			}
		}
	}

	private static void parseFeatureDefinition(FeatureDefinition fd){
		final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
		sftb.setName(fd.Name);
		final AttributeTypeBuilder atb = new AttributeTypeBuilder();
		Class geomClass = null;
		switch (fd.Type){
			case Geometry : {
				geomClass = Geometry.class;
				break;
			}
			case Point : {
				geomClass = Point.class;
				break;
			}
			case LineString : {
				geomClass = LineString.class;
				break;
			}
			case Polygon : {
				geomClass = Polygon.class;
			}
		}
		//sftb.add(atb.binding(geomClass).nillable(false).buildDescriptor("geometry"));
		for (AttributeDefinition ad : fd.Attributes){
			AttributeType at = AttributeTypes.getAttributeType(ad.Type);
			if (ad.Name == null){
				System.out.println("yo");
			}
			if (at != null){
				sftb.add(atb.binding(at.getClassType()).nillable(true).buildDescriptor(normalizeOsmNames(ad.Name)));
			}
		}
		SimpleFeatureType sft = sftb.buildFeatureType();
		featureTypes.put(fd.Name, sft);
		featureAdapters.put(fd.Name, new FeatureDataAdapter(sft));
	}

	public static String normalizeOsmNames(String name){
		if (name == null)
			return null;

		return name.trim().toLowerCase().replace(":", "_");
	}


}
