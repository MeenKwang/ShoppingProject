import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BrandService {

  private base_url = "http://localhost:8080/ShoppingAdmin/";

  constructor(
    private httpClient: HttpClient
  ) { }
  public listFirstPage() {
    return this.httpClient.get(this.base_url + "api/categories");
  }

  public listBrandsByPage(pageNum : number | any, nameSearch : string | any, sortField : string, sortDir : string,
    pageSize : number) {

    let params = new HttpParams();
    params = params.append('nameSearch', nameSearch);
    params = params.append('sortField', sortField);
    params = params.append('sortDir', sortDir);
    params = params.append('pageSize', pageSize);
    
    return this.httpClient.get(`${this.base_url}api/categories/page/${pageNum}`, { params : params }).pipe();
  }

}
