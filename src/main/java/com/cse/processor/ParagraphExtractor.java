package com.cse.processor;

import com.clearspring.analytics.util.Lists;
import com.cse.entity.Page;
import com.cse.entity.Paragraph;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bullet on 16. 10. 25.
 */
public class ParagraphExtractor implements Serializable {
    private JavaRDD<Page> pageRDD;

    public ParagraphExtractor(JavaRDD<Page> pageRDD){
        this.pageRDD = pageRDD;
    }

    public JavaRDD<Paragraph> pageRddToParagraphRDD(){
        return pageRDD.flatMap(new FlatMapFunction<Page, Paragraph>() {
            @Override
            public Iterable<Paragraph> call(Page page) throws Exception {
                String body = page.getBody();
                int id = page.getId();

                body = body.replaceAll("\n", "");
                List<Paragraph> paragraphList = Lists.newArrayList();

                String[] paragraphArr = body.split("\\. |\\? |! ");

                for(int i=0; i<paragraphArr.length; i++){
                    String s = paragraphArr[i];
                    paragraphList.add(new Paragraph(id, s));
                }

                return paragraphList;
            }
        });
    }
}
