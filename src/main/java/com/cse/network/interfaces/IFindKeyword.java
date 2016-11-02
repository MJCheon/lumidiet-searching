package com.cse.network.interfaces;

import com.cse.network.data.OutboundData;

/**
 * Created by bullet on 16. 10. 25.
 */
public interface IFindKeyword {
    void findSynData(OutboundData synData);
    void error();
}
