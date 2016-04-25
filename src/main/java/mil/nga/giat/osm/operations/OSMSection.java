package mil.nga.giat.osm.operations;

import com.beust.jcommander.Parameters;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;
import mil.nga.giat.geowave.core.cli.api.DefaultOperation;
import mil.nga.giat.geowave.core.cli.operations.GeowaveTopLevelSection;

@GeowaveOperation(name = "osm", parentOperation = GeowaveTopLevelSection.class)
@Parameters(commandDescription = "Operations to ingest OSM nodes, ways and relations to GeoWave")
public class OSMSection extends DefaultOperation {

}
