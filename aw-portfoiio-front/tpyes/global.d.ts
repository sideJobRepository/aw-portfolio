export {};

//주소검색 type
declare global {
  interface Window {
    daum: {
      Postcode: new (options: any) => any;
    };
  }
}
