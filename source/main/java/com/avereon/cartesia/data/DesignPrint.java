package com.avereon.cartesia.data;

import lombok.CustomLog;

import java.util.HashMap;
import java.util.Map;

@CustomLog
public class DesignPrint extends DesignNode {

	public Map<String,Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		//if( getLayers().size() > 0 ) map.put( LAYERS, getLayers().stream().map( IdNode::getId ).collect( Collectors.toSet()) );
		return map;
	}


}
