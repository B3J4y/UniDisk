package de.unidisk.view.results;

import de.unidisk.view.project.Project;
import de.unidisk.view.variables.VariablesView;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "resultService")
public class ResultService {
    private PodamFactory factory = new PodamFactoryImpl();

    private List<ResultBean> resultBeans;

    @PostConstruct
    public void init() {
        resultBeans = getResultBeans(12);
    }

    public List<ResultBean> getResultBeans(int i) {


        ArrayList<ResultBean> resultBeans = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            ResultBean result = factory.manufacturePojo(ResultBean.class);
            resultBeans.add(result);
        }
        return resultBeans;
    }

    public List<String> getVariables() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<ResultBean> results = getResultBeans(12);
        for (ResultBean result : results) {
            arrayList.add(result.getVariable());
        }
        return arrayList;
    }

    public List<String> getWebsites() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<ResultBean> results = getResultBeans(12);
        for (ResultBean result : results) {
            arrayList.add(result.getWebsite());
        }
        return arrayList;
    }

    public List<ResultBean> getResultBeans() {
        return resultBeans;
    }

    public void setResultBeans(List<ResultBean> resultBeans) {
        this.resultBeans = resultBeans;
    }
}
