package db;

import beans.Result;

import jakarta.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class Results {

    private List<Result> resultList = new ArrayList<>();
    public Results(){};

    public void add(Result result){
        resultList.add(result);
    }
    public void clear(){
        resultList.clear();
    }
    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }
}
