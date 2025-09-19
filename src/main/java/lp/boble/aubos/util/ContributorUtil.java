package lp.boble.aubos.util;

import lp.boble.aubos.dto.book.parts.BookContributorPartResponse;
import lp.boble.aubos.model.book.relationships.BookContributorModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributorUtil {

    public static Map<String, List<BookContributorPartResponse>> arrangeContributors(List<BookContributorModel> rawContributors){
        Map<String, List<BookContributorPartResponse>> contributors = new HashMap<>();
        contributors.put("autor", new ArrayList<>());
        contributors.put("editor", new ArrayList<>());
        contributors.put("ilustrador", new ArrayList<>());
        contributors.put("publicadora", new ArrayList<>());

        rawContributors.forEach(c ->
                contributors.get(c.getContributorRole().getName())
                        .add(new BookContributorPartResponse(
                                c.getContributor().getId(),
                                c.getContributor().getName()
                        ))
        );

        return contributors;
    }

}
