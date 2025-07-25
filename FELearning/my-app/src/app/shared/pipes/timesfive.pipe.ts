import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timesfive',
})
export class TimesfivePipe implements PipeTransform {
  transform(value: number): number {
    return value * 5;
  }
}
