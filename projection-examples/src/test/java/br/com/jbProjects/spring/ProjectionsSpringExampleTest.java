package br.com.jbProjects.spring;

import br.com.jbProjects.BaseApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by julio.bueno on 27/11/2025.
 */
class ProjectionsSpringExampleTest extends BaseApplicationTest {

    @Autowired
    private ProjectionsSpringExample examples;

    @Test
    public void fetchCustomerBasicDataClass(){
        examples.fetchCustomerBasicDataClass();
    }

    @Test
    public void fetchCustomerBasicDataRecord() {
        examples.fetchCustomerBasicDataRecord();
    }

    @Test
    public void fetchWithProjectionQuery() {
        examples.fetchWithProjectionQuery();
    }

    @Test
    public void fetchWithFilterAndSpecification() {
        examples.fetchWithFilterAndSpecification();
    }

    @Test
    public void fetchWithStringFilterOperation() {
        examples.fetchWithStringFilterOperation();
    }

    @Test
    public void fetchWithNestedAttributeFilter() {
        examples.fetchWithNestedAttributeFilter();
    }

    @Test
    public void fetchWithNestedAttributesSelect() {
        examples.fetchWithNestedAttributesSelect();
    }

    @Test
    public void fetchWithFiltersUtilities(){
        examples.fetchWithFiltersUtilities();
    }

    @Test
    public void fetchWithAggregateAttributes() {
        examples.fetchWithAggregateAttributes();
    }

    @Test
    public void fetchWithJoinAlias() {
        examples.fetchWithJoinAlias();
    }

    @Test
    public void fetchFilteringByAliasPath() {
        examples.fetchFilteringByAliasPath();
    }

    @Test
    public void fetchWithCompoundFilter(){
        examples.fetchWithCompoundFilter();
    }

    @Test
    public void fetchWithCompoundFilterUsingOperator(){
        examples.fetchWithCompoundFilterUsingOperator();
    }

    @Test
    public void fetchWithPageableResult(){
        examples.fetchWithPageableResult();
    }
}
