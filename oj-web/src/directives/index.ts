import type { App, Directive } from 'vue';
import auth from '@/directives/modules/auth';
import copy from '@/directives/modules/copy';

const directivesList: { [key: string]: Directive } = {
  auth,
  copy
};

const directives = {
  install: function (app: App<Element>) {
    Object.keys(directivesList).forEach(key => {
      app.directive(key, directivesList[key]);
    });
  }
};

export default directives;
